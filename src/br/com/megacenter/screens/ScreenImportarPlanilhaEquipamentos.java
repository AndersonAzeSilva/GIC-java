package br.com.megacenter.screens;

import br.com.megacenter.dal.ModuloConexao;
import br.com.megacenter.dto.EquipamentoImportacaoDTO;
import br.com.megacenter.services.EquipamentoImportacaoPersistService;
import br.com.megacenter.services.ImportacaoArquivoStore;
import br.com.megacenter.services.ImportacaoExcelRegistroService;
import br.com.megacenter.services.ImportacaoExcelService;
import br.com.megacenter.services.ImportacoesExcelService;
import br.com.megacenter.ui.ImportacaoStatusRenderer;
import br.com.megacenter.ui.ValidacaoRenderer;

import java.awt.Cursor;
import java.awt.Desktop;
import java.io.File;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;

import net.proteanit.sql.DbUtils;

public class ScreenImportarPlanilhaEquipamentos extends javax.swing.JInternalFrame {

    private List<EquipamentoImportacaoDTO> listaImportada;
    private Integer idImportacaoAtual = null;

    public ScreenImportarPlanilhaEquipamentos() {
        initComponents();
        configurarTabelaPreview();
        configurarTabelaHistoricoImportacoes();
        carregarHistoricoImportacoesAsync();
    }

    // =====================================================
    // CONFIGURA PREVIEW
    // =====================================================
    private void configurarTabelaPreview() {

        DefaultTableModel model = new DefaultTableModel();
        model.setColumnIdentifiers(new String[]{
            "Etiqueta", "Filial", "Tipo", "Descrição", "Setor", "Usuário",
            "Valor", "Qtd", "Empresa", "Entrada", "Saída",
            "Status", "Marca", "Condições", "Validação"
        });

        tblHistoricoDeEqui.setModel(model);

        int colValidacao = model.getColumnCount() - 1;
        tblHistoricoDeEqui.getColumnModel().getColumn(colValidacao)
                .setCellRenderer(new ValidacaoRenderer());

        tblHistoricoDeEqui.setRowHeight(28);
    }

    // =====================================================
    // CONFIGURA HISTÓRICO DE IMPORTAÇÕES (tblimportacoes_excel)
    // =====================================================
    private void configurarTabelaHistoricoImportacoes() {

        DefaultTableModel model = new DefaultTableModel(
                new Object[][]{},
                new String[]{
                    "ID", "Data", "Usuário", "Arquivo",
                    "Total", "Válidas", "Inválidas", "Observação", "Caminho"
                }
        );

        tblimportacoes_excel.setModel(model);
        tblimportacoes_excel.setRowHeight(26);

        esconderColunaCaminhoHistorico();

        tblimportacoes_excel.setDefaultRenderer(Object.class, new ImportacaoStatusRenderer(6));

        tblimportacoes_excel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    abrirArquivoHistoricoSelecionado();
                }
            }
        });
    }

    private void esconderColunaCaminhoHistorico() {
        if (tblimportacoes_excel.getColumnModel().getColumnCount() > 8) {
            tblimportacoes_excel.getColumnModel().getColumn(8).setMinWidth(0);
            tblimportacoes_excel.getColumnModel().getColumn(8).setMaxWidth(0);
            tblimportacoes_excel.getColumnModel().getColumn(8).setPreferredWidth(0);
        }
    }

    private void carregarHistoricoImportacoesAsync() {

        new SwingWorker<ResultSet, Void>() {

            @Override
            protected ResultSet doInBackground() throws Exception {
                return ImportacoesExcelService.buscarTodas();
            }

            @Override
            protected void done() {
                try {
                    ResultSet rs = get();
                    tblimportacoes_excel.setModel(DbUtils.resultSetToTableModel(rs));
                    tblimportacoes_excel.setRowHeight(26);
                    esconderColunaCaminhoHistorico();
                    tblimportacoes_excel.setDefaultRenderer(Object.class, new ImportacaoStatusRenderer(6));
                } catch (Exception e) {
                    System.err.println("Falha ao carregar histórico importações: " + e.getMessage());
                }
            }
        }.execute();
    }

    private void abrirArquivoHistoricoSelecionado() {
        try {
            int row = tblimportacoes_excel.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Selecione uma importação no histórico.");
                return;
            }

            int colCaminho = tblimportacoes_excel.getColumnCount() - 1;
            Object caminhoObj = tblimportacoes_excel.getValueAt(row, colCaminho);

            String caminho = caminhoObj == null ? "" : caminhoObj.toString().trim();
            if (caminho.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Caminho do arquivo não encontrado.");
                return;
            }

            File f = new File(caminho);
            if (!f.exists()) {
                JOptionPane.showMessageDialog(this, "Arquivo não existe:\n" + caminho);
                return;
            }

            Desktop.getDesktop().open(f);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao abrir arquivo:\n" + e.getMessage());
        }
    }

    // =====================================================
    // IMPORTAR (PREVIEW)
    // =====================================================
    private void importarExcel() {

        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new FileNameExtensionFilter("Planilha Excel (*.xlsx)", "xlsx"));

        if (chooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File arquivo = chooser.getSelectedFile();
        String usuario = lblUsuarioLogado.getText().trim();

        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        btnImportarExcel.setEnabled(false);
        btnSalvarImportacao.setEnabled(false);
        btnLimparPreview.setEnabled(false);

        new SwingWorker<List<EquipamentoImportacaoDTO>, Void>() {

            private Path caminhoSalvo;

            @Override
            protected List<EquipamentoImportacaoDTO> doInBackground() throws Exception {

                // 1) salva cópia do arquivo
                caminhoSalvo = ImportacaoArquivoStore.salvarCopia(arquivo);

                // 2) cria registro inicial
                try (Connection conexao = ModuloConexao.conector()) {
                    idImportacaoAtual = ImportacaoExcelRegistroService.criarRegistroInicial(
                            conexao,
                            arquivo.getName(),
                            caminhoSalvo.toString(),
                            usuario
                    );
                }

                // 3) lê excel (preview)
                return ImportacaoExcelService.lerExcel(arquivo);
            }

            @Override
            protected void done() {
                try {
                    listaImportada = get();

                    validarListaImportada(listaImportada);
                    preencherPreviewTabela(listaImportada);

                    int total = listaImportada.size();
                    int validas = 0;
                    int invalidas = 0;

                    for (EquipamentoImportacaoDTO e : listaImportada) {
                        if (e.valido) {
                            validas++;
                        } else {
                            invalidas++;
                        }
                    }

                    try (Connection conexao = ModuloConexao.conector()) {
                        ImportacaoExcelRegistroService.atualizarTotais(
                                conexao,
                                idImportacaoAtual,
                                total, validas, invalidas,
                                "Preview carregado. Clique em 'Salvar Importação' para gravar no banco."
                        );
                    }

                    JOptionPane.showMessageDialog(
                            ScreenImportarPlanilhaEquipamentos.this,
                            "Planilha carregada.\nTotal: " + total
                            + " | Válidas: " + validas + " | Inválidas: " + invalidas
                            + "\n\nConfira e depois clique em 'Salvar Importação'."
                    );

                    carregarHistoricoImportacoesAsync();

                } catch (Exception e) {
                    JOptionPane.showMessageDialog(
                            ScreenImportarPlanilhaEquipamentos.this,
                            "Erro ao importar Excel:\n" + e.getMessage()
                    );
                } finally {
                    setCursor(Cursor.getDefaultCursor()); // ✅ corrigido
                    btnImportarExcel.setEnabled(true);
                    btnSalvarImportacao.setEnabled(true);
                    btnLimparPreview.setEnabled(true);
                }
            }
        }.execute();
    }

    // =====================================================
    // PREENCHE PREVIEW
    // =====================================================
    private void preencherPreviewTabela(List<EquipamentoImportacaoDTO> lista) {

        DefaultTableModel model = (DefaultTableModel) tblHistoricoDeEqui.getModel();
        model.setRowCount(0);

        for (EquipamentoImportacaoDTO e : lista) {
            model.addRow(new Object[]{
                e.etiqueta,
                e.filial,
                e.tipo,
                e.descricao,
                e.setor,
                e.funcionario,
                e.valor,
                e.quantidade,
                e.empresa,
                e.dataEntrada,
                e.dataSaida,
                e.status,
                e.marca,
                e.condicoes,
                e.valido ? "OK" : "ERRO"
            });
        }

        int colValidacao = model.getColumnCount() - 1;
        tblHistoricoDeEqui.getColumnModel().getColumn(colValidacao)
                .setCellRenderer(new ValidacaoRenderer());
    }

    // =====================================================
    // VALIDAÇÃO MÍNIMA (para salvar)
    // =====================================================
    private void validarListaImportada(List<EquipamentoImportacaoDTO> lista) {

        for (EquipamentoImportacaoDTO e : lista) {

            e.valido = true;
            e.erro = "";

            if (e.etiqueta == null) {
                e.valido = false;
                e.erro += "Etiqueta vazia/inválida. ";
            }
            if (e.filial == null) {
                e.valido = false;
                e.erro += "Filial vazia/inválida. ";
            }
        }
    }

    // =====================================================
    // SALVAR IMPORTAÇÃO
    // =====================================================
    private void salvarImportacao() {

        if (listaImportada == null || listaImportada.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Importe uma planilha primeiro.");
            return;
        }

        if (idImportacaoAtual == null) {
            JOptionPane.showMessageDialog(this, "ID da importação não encontrado. Reimporte a planilha.");
            return;
        }

        validarListaImportada(listaImportada);
        preencherPreviewTabela(listaImportada);

        int confirmar = JOptionPane.showConfirmDialog(
                this,
                "Deseja salvar os itens válidos no banco?\nItens inválidos serão ignorados.",
                "Salvar Importação",
                JOptionPane.YES_NO_OPTION
        );

        if (confirmar != JOptionPane.YES_OPTION) {
            return;
        }

        String usuario = lblUsuarioLogado.getText().trim();

        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        btnSalvarImportacao.setEnabled(false);

        new SwingWorker<Integer, Void>() {

            @Override
            protected Integer doInBackground() throws Exception {
                return EquipamentoImportacaoPersistService.salvarImportacao(
                        listaImportada,
                        usuario,
                        idImportacaoAtual
                );
            }

            @Override
            protected void done() {
                try {
                    int gravados = get();
                    JOptionPane.showMessageDialog(
                            ScreenImportarPlanilhaEquipamentos.this,
                            "Importação concluída.\nRegistros gravados: " + gravados
                    );

                    carregarHistoricoImportacoesAsync();

                } catch (Exception e) {
                    JOptionPane.showMessageDialog(
                            ScreenImportarPlanilhaEquipamentos.this,
                            "Erro ao salvar importação:\n" + e.getMessage()
                    );
                } finally {
                    setCursor(Cursor.getDefaultCursor());
                    btnSalvarImportacao.setEnabled(true);
                }
            }
        }.execute();
    }

    // =====================================================
    // LIMPAR PREVIEW
    // =====================================================
    private void limparPreview() {

        listaImportada = null;
        idImportacaoAtual = null;

        DefaultTableModel model = (DefaultTableModel) tblHistoricoDeEqui.getModel();
        model.setRowCount(0);

        JOptionPane.showMessageDialog(this, "Pré-visualização limpa.");
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txtPesquisaHistoricoEqui = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        tblHistoricoDeEqui = new javax.swing.JTable();
        txtFiltroHistoricoEqui = new javax.swing.JTextField();
        jLabel16 = new javax.swing.JLabel();
        cboFiltroHistoricoTipoEqui = new javax.swing.JComboBox();
        jLabel21 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        cboFiltroHistoricoUsuarioEqui = new javax.swing.JComboBox();
        bntBuscarHistoricoEqui = new javax.swing.JButton();
        dtSaidaHistoricoEqui = new com.toedter.calendar.JDateChooser();
        dtEntradaHistoricoEqui = new com.toedter.calendar.JDateChooser();
        jLabel11 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        cboFiltroHistoricoFilialEqui = new javax.swing.JComboBox();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblimportacoes_excel = new javax.swing.JTable();
        btnLimparPreview = new javax.swing.JButton();
        btnSalvarImportacao = new javax.swing.JButton();
        btnImportarExcel = new javax.swing.JButton();
        lblUsuarioLogado = new javax.swing.JLabel();

        setBackground(new java.awt.Color(255, 255, 255));
        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setTitle("G&C - Importar Planilha de Equipamentos");

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Registro de importação", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 13))); // NOI18N

        jLabel1.setText("Equipamento");

        jLabel2.setText("Filial");

        tblHistoricoDeEqui.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Nº Etiqueta", "Filial", "Tipo", "Descrição", "Setor", "Usuário", "Valor", "Quantidade", "Fornecedor", "Data Entrada", "Data Saída", "Status", "Marca", "Condição"
            }
        ));
        tblHistoricoDeEqui.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblHistoricoDeEquiMouseClicked(evt);
            }
        });
        jScrollPane4.setViewportView(tblHistoricoDeEqui);

        txtFiltroHistoricoEqui.setBackground(new java.awt.Color(255, 255, 204));
        txtFiltroHistoricoEqui.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtFiltroHistoricoEquiKeyReleased(evt);
            }
        });

        jLabel16.setText("Descrição");

        cboFiltroHistoricoTipoEqui.setBackground(new java.awt.Color(255, 255, 204));
        cboFiltroHistoricoTipoEqui.setModel(new javax.swing.DefaultComboBoxModel(new String[] { " " }));

        jLabel21.setText("Tipo");

        jLabel22.setText("Usuário");

        cboFiltroHistoricoUsuarioEqui.setBackground(new java.awt.Color(255, 255, 204));
        cboFiltroHistoricoUsuarioEqui.setModel(new javax.swing.DefaultComboBoxModel(new String[] { " ", " " }));

        bntBuscarHistoricoEqui.setBackground(new java.awt.Color(255, 255, 204));
        bntBuscarHistoricoEqui.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        bntBuscarHistoricoEqui.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/megacenter/icones/Icon_Pesquisa.png"))); // NOI18N
        bntBuscarHistoricoEqui.setText("Buscar");
        bntBuscarHistoricoEqui.setPreferredSize(new java.awt.Dimension(69, 25));
        bntBuscarHistoricoEqui.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bntBuscarHistoricoEquiActionPerformed(evt);
            }
        });

        jLabel11.setText("Dt. de Entrada");

        jLabel9.setText("Dt. de Saída");

        cboFiltroHistoricoFilialEqui.setBackground(new java.awt.Color(255, 255, 204));
        cboFiltroHistoricoFilialEqui.setModel(new javax.swing.DefaultComboBoxModel(new String[] { " " }));

        tblimportacoes_excel.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "Data_Importação", "Usuário", "Nome_Arquivo", "Caminho_Arquivo", "Total", "Validas", "Invalidas", "Observação", "Status"
            }
        ));
        jScrollPane1.setViewportView(tblimportacoes_excel);

        btnLimparPreview.setText("Limpar Campos");
        btnLimparPreview.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLimparPreviewActionPerformed(evt);
            }
        });

        btnSalvarImportacao.setText("Salvar Importação");
        btnSalvarImportacao.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSalvarImportacaoActionPerformed(evt);
            }
        });

        btnImportarExcel.setText("Importar Execel");
        btnImportarExcel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnImportarExcelActionPerformed(evt);
            }
        });

        lblUsuarioLogado.setText("Usuário");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addComponent(jScrollPane4)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(txtPesquisaHistoricoEqui, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addGap(50, 50, 50))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addComponent(cboFiltroHistoricoFilialEqui, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)))
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtFiltroHistoricoEqui, javax.swing.GroupLayout.PREFERRED_SIZE, 223, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(cboFiltroHistoricoTipoEqui, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel22, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cboFiltroHistoricoUsuarioEqui, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(dtEntradaHistoricoEqui, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(dtSaidaHistoricoEqui, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 16, Short.MAX_VALUE)
                        .addComponent(bntBuscarHistoricoEqui, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(lblUsuarioLogado)
                        .addGap(18, 18, 18)
                        .addComponent(btnLimparPreview)
                        .addGap(18, 18, 18)
                        .addComponent(btnSalvarImportacao)
                        .addGap(18, 18, 18)
                        .addComponent(btnImportarExcel)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1)
                            .addComponent(jLabel2))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtPesquisaHistoricoEqui, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel16)
                        .addGap(7, 7, 7)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtFiltroHistoricoEqui, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cboFiltroHistoricoFilialEqui, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel21)
                        .addGap(7, 7, 7)
                        .addComponent(cboFiltroHistoricoTipoEqui, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel22)
                        .addGap(6, 6, 6)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(cboFiltroHistoricoUsuarioEqui, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(bntBuscarHistoricoEqui, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel11)
                        .addGap(10, 10, 10)
                        .addComponent(dtEntradaHistoricoEqui, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel9)
                        .addGap(10, 10, 10)
                        .addComponent(dtSaidaHistoricoEqui, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 192, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnImportarExcel)
                    .addComponent(btnSalvarImportacao)
                    .addComponent(btnLimparPreview)
                    .addComponent(lblUsuarioLogado))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 266, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void tblHistoricoDeEquiMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblHistoricoDeEquiMouseClicked
        // chamando o metodo preencher campos
        //exibir_campos();
    }//GEN-LAST:event_tblHistoricoDeEquiMouseClicked

    private void txtFiltroHistoricoEquiKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtFiltroHistoricoEquiKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_txtFiltroHistoricoEquiKeyReleased

    private void bntBuscarHistoricoEquiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bntBuscarHistoricoEquiActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_bntBuscarHistoricoEquiActionPerformed

    private void btnImportarExcelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnImportarExcelActionPerformed
        // método que já criamos
        importarExcel();
    }//GEN-LAST:event_btnImportarExcelActionPerformed

    private void btnSalvarImportacaoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSalvarImportacaoActionPerformed
        // TODO add your handling code here:
        salvarImportacao();
    }//GEN-LAST:event_btnSalvarImportacaoActionPerformed

    private void btnLimparPreviewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLimparPreviewActionPerformed
        // TODO add your handling code here:
        limparPreview();
    }//GEN-LAST:event_btnLimparPreviewActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton bntBuscarHistoricoEqui;
    private javax.swing.JButton btnImportarExcel;
    private javax.swing.JButton btnLimparPreview;
    private javax.swing.JButton btnSalvarImportacao;
    private javax.swing.JComboBox cboFiltroHistoricoFilialEqui;
    private javax.swing.JComboBox cboFiltroHistoricoTipoEqui;
    private javax.swing.JComboBox cboFiltroHistoricoUsuarioEqui;
    private com.toedter.calendar.JDateChooser dtEntradaHistoricoEqui;
    private com.toedter.calendar.JDateChooser dtSaidaHistoricoEqui;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JLabel lblUsuarioLogado;
    private javax.swing.JTable tblHistoricoDeEqui;
    private javax.swing.JTable tblimportacoes_excel;
    private javax.swing.JTextField txtFiltroHistoricoEqui;
    private javax.swing.JTextField txtPesquisaHistoricoEqui;
    // End of variables declaration//GEN-END:variables
}
