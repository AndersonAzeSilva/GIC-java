/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.megacenter.screens;

import java.sql.*;
import br.com.megacenter.dal.ModuloConexao;
import java.util.HashMap;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import net.proteanit.sql.DbUtils;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.view.JasperViewer;
import javax.swing.GroupLayout;
/**
 *
 * @author JANDERSON
 */
public class ScreenChamados extends javax.swing.JInternalFrame {

    Connection conexao = null;
    PreparedStatement pst = null;
    ResultSet rs = null;

    // a linha abaixo cria uma varial para armazenar um texto de acordo com o radio
    // butom selecionado
    private String tipo;

    /**
     * Creates new form ScreenChamado
     */
    public ScreenChamados() {
        initComponents();
        conexao = ModuloConexao.conector();
    }
// criando o metodo pesquisar empresa

    private void pesquisar_empresa() {
        String sql = "select idempre as Código, razao_social as Nome, telefone as Telefone from empresas where razao_social like?";
        try {
            pst = conexao.prepareStatement(sql);
            pst.setString(1, txtPesquisarEmpreChamado.getText() + "%");
            rs = pst.executeQuery();
            tblEmpresaChamado.setModel(DbUtils.resultSetToTableModel(rs));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }

    // criando o metodo para setar os campos
    private void preencher_campos() {
        int preencher = tblEmpresaChamado.getSelectedRow();
        txtIdEmpreChamado.setText(tblEmpresaChamado.getModel().getValueAt(preencher, 0).toString());
    }

    // metodo para emitir um chamado
    private void emitir_chamado() {
        String sql = "insert into chamados (tipo, valor, equipamento, titulo, descricao, categoria, grupo, urgencia, status, atribuido, idempre) "
                + "values (?,?,?,?,?,?,?,?,?,?,?)";
        try {
            // a linha abixo prepara a conexão
            pst = conexao.prepareStatement(sql);
            pst.setString(1, tipo);
            //.replace substitui o . pela virgula no campo valor
            pst.setString(2, txtValorChamado.getText().replace(",", "."));
            pst.setString(3, txtEquipamentoChamado.getText());
            pst.setString(4, txtTituloChamado.getText());
            pst.setString(5, txtDescricaoChamado.getText());
            pst.setString(6, cboCategoriaChamado.getSelectedItem().toString());
            pst.setString(7, cboGrupoChamado.getSelectedItem().toString());
            pst.setString(8, cboUrgenciaChamado.getSelectedItem().toString());
            pst.setString(9, cboStatusChamado.getSelectedItem().toString());
            pst.setString(10, cboAtribuidoChamado.getSelectedItem().toString());
            pst.setString(11, txtIdEmpreChamado.getText());

            // craindo o metodo para validar os campos obrigatórios
            if ((txtIdEmpreChamado.getText().isEmpty()) || (txtEquipamentoChamado.getText().isEmpty())
                    || (txtTituloChamado.getText().isEmpty()) || (txtDescricaoChamado.getText().isEmpty())
                    || (cboCategoriaChamado.getSelectedItem().equals(" ") || (cboGrupoChamado.getSelectedItem().equals(" ")
                    || cboUrgenciaChamado.getSelectedItem().equals(" ") || cboStatusChamado.getSelectedItem().equals(" ")
                    || cboAtribuidoChamado.getSelectedItem().equals(" ")))) {
                JOptionPane.showMessageDialog(null, "Por favor, Preencha todos os campos obrigatórios.");
            } else {
                int adicionado = pst.executeUpdate();
                if (adicionado > 0) {
                    JOptionPane.showMessageDialog(null, "Chamado criado com sucesso!");
                    // chamadno o metodo para recuperar o número do chamado
                    recuperarChamado();
                    // desabilitando e habilitando os botões
                   btnAdicionarChamado.setEnabled(false);
                   btnPesquisarChamado.setEnabled(false);
                   btnImprimirChamado.setEnabled(true);
                    

                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }

    // craindo o metodo para pesquisar um chamado
    private void pesquisar_chamado() {
        // a linha abaixo cria uma caixa de entrada do tipo JOption Pane
        String num_chamado = JOptionPane.showInputDialog("Informe o número do chamado");
        String sql = "select idchamado, date_format(data_chamado,'%d/%m/%Y - %H:%i'), tipo, valor, "
                + "equipamento, titulo, descricao, categoria, grupo, urgencia, status, atribuido, "
                + "idempre from chamados where idchamado = " + num_chamado;
        try {
            pst = conexao.prepareStatement(sql);
            rs = pst.executeQuery();
            if (rs.next()) {
                txtIdChamado.setText(rs.getString(1));
                txtDataChamado.setText(rs.getString(2));
                // setabdi is radio buttons
                String rbtTipo = rs.getString(3);
                if (rbtTipo.equals("chamado")) {
                    rbtChamado.setSelected(true);
                    tipo = "chamado";
                } else {
                    rbtOrcamentoChamado.setSelected(true);
                    tipo = "Orçamento";
                }
                txtValorChamado.setText(rs.getString(4));
                txtEquipamentoChamado.setText(rs.getString(5));
                txtTituloChamado.setText(rs.getString(6));
                txtDescricaoChamado.setText(rs.getString(7));
                cboCategoriaChamado.setSelectedItem(rs.getString(8));
                cboGrupoChamado.setSelectedItem(rs.getString(9));
                cboUrgenciaChamado.setSelectedItem(rs.getString(10));
                cboStatusChamado.setSelectedItem(rs.getString(11));
                cboAtribuidoChamado.setSelectedItem(rs.getString(12));
                //Evitando problemas
                btnAdicionarChamado.setEnabled(false);
                btnPesquisarChamado.setEnabled(false);
                txtPesquisarEmpreChamado.setEnabled(false);
                tblEmpresaChamado.setVisible(false);
                // ativando os demias botões
                btnAlterarChamado.setEnabled(true);
                btnExcluirChamado.setEnabled(true);
                btnImprimirChamado.setEnabled(true);
            } else {
                JOptionPane.showMessageDialog(null, "Chamado não cadastrado");
            }
        } catch (java.sql.SQLSyntaxErrorException e) {
            JOptionPane.showMessageDialog(null, "Este tipo de chamado é inválido!");
            //System.out.println(e);
        } catch (Exception e2) {
            JOptionPane.showMessageDialog(null, e2);
        }
    }

    // criando o metodo para alterar um chamado
    private void alterar_chamado() {
        String sql = "update chamados set tipo=?, valor=?, equipamento=?, titulo=?, descricao=?, categoria=?, "
                + "grupo=?, urgencia=?, status=?, atribuido=? where idchamado=?";
        try {
            // a linha abixo prepara a conexão
            pst = conexao.prepareStatement(sql);
            pst.setString(1, tipo);
            //.replace substitui o . pela virgula no campo valor
            pst.setString(2, txtValorChamado.getText().replace(",", "."));
            pst.setString(3, txtEquipamentoChamado.getText());
            pst.setString(4, txtTituloChamado.getText());
            pst.setString(5, txtDescricaoChamado.getText());
            pst.setString(6, cboCategoriaChamado.getSelectedItem().toString());
            pst.setString(7, cboGrupoChamado.getSelectedItem().toString());
            pst.setString(8, cboUrgenciaChamado.getSelectedItem().toString());
            pst.setString(9, cboStatusChamado.getSelectedItem().toString());
            pst.setString(10, cboAtribuidoChamado.getSelectedItem().toString());
            pst.setString(11, txtIdChamado.getText());

            // craindo o metodo para validar os campos obrigatórios
            if ((txtIdChamado.getText().isEmpty()) || (txtEquipamentoChamado.getText().isEmpty())
                    || (txtTituloChamado.getText().isEmpty()) || (txtDescricaoChamado.getText().isEmpty())
                    || (cboCategoriaChamado.getSelectedItem().equals(" ") || (cboGrupoChamado.getSelectedItem().equals(" ")
                    || cboUrgenciaChamado.getSelectedItem().equals(" ") || cboStatusChamado.getSelectedItem().equals(" ")
                    || cboAtribuidoChamado.getSelectedItem().equals(" ")))) {
                JOptionPane.showMessageDialog(null, "Por favor, Preencha todos os campos obrigatórios.");
            } else {
                int adicionado = pst.executeUpdate();
                if (adicionado > 0) {
                    JOptionPane.showMessageDialog(null, "Chamado alterado com sucesso!");
                    // chamando o metodo limpando os campos
                    limpar_campos();
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }

    //criando o método para excluir um chamado
    private void excluir_chamado() {
        int confirma = JOptionPane.showConfirmDialog(null, "Você tem certeza que deseja "
                + "excluir este chamado?", "Atenção", JOptionPane.YES_NO_OPTION);
        if (confirma == JOptionPane.YES_OPTION) {
            String sql = "delete from chamados where idchamado = ?";
            try {
                pst = conexao.prepareStatement(sql);
                pst.setString(1, txtIdChamado.getText());
                int excluido = pst.executeUpdate();
                if (excluido > 0) {
                    JOptionPane.showMessageDialog(null, "Chamado excluido com sucesso!");
                    // chamando o metodo limpando os campos
                    limpar_campos();
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, e);
            }
        }
    }
    
    // criando o metodo para imprimir um chamado
    private void imprimir_chamado(){
                // imprimindo um chamado
                int confirma = JOptionPane.showConfirmDialog(null, "Você confirma a impressão desse chamado?","Atenção",JOptionPane.YES_NO_OPTION);
        if (confirma == JOptionPane.YES_OPTION){
            // imprimindo o chamado com o framework JasperReports
            try {
                // usando a classe HashMap para criar um filtro
                HashMap filtro = new HashMap();
                filtro.put("chamado",Integer.parseInt(txtIdChamado.getText()));
                //Usando a classe JasperPrint para preprar a impressão de um relatório
                JasperPrint print = JasperFillManager.fillReport(getClass().getResourceAsStream("/reports/imprechamado.jasper"), filtro, conexao);
                // a linha a seguir exibi o relat´rio através da classe JasperViwer
                JasperViewer.viewReport(print,false);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, e);
            }
        }
    }
    
    // criando metodo para recuperar o chamado
    private void recuperarChamado(){
        String sql = "select max(idchamado) from chamados";
            try {
                pst = conexao.prepareStatement(sql);
                rs = pst.executeQuery();
                if (rs.next()){
                    txtIdChamado.setText(rs.getString(1));
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, e);
            }
    }

    // limpar campos e habilitar os botões e gerenciar os botões
    private void limpar_campos() {
        // limpando os campos
        txtIdChamado.setText(null);
        txtDataChamado.setText(null);
        cboStatusChamado.setSelectedItem(" ");
        txtEquipamentoChamado.setText(null);
        txtTituloChamado.setText(null);
        txtDescricaoChamado.setText(null);
        cboCategoriaChamado.setSelectedItem(" ");
        cboGrupoChamado.setSelectedItem(" ");
        cboUrgenciaChamado.setSelectedItem(" ");
        txtValorChamado.setText(null);
        cboAtribuidoChamado.setSelectedItem(" ");
        txtIdEmpreChamado.setText(null);
        ((DefaultTableModel) tblEmpresaChamado.getModel()).setRowCount(0); // limpando os dados da tabela
        //habilitando novamente os objetos
        btnAdicionarChamado.setEnabled(true);
        btnPesquisarChamado.setEnabled(true);
        txtIdChamado.setEnabled(true);
        txtIdEmpreChamado.setVisible(true);
        // desabilitar os botões
        btnAlterarChamado.setEnabled(false);
        btnExcluirChamado.setEnabled(false);
        btnImprimirChamado.setEnabled(false);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        txtIdChamado = new javax.swing.JTextField();
        txtDataChamado = new javax.swing.JTextField();
        rbtOrcamentoChamado = new javax.swing.JRadioButton();
        rbtChamado = new javax.swing.JRadioButton();
        jLabel12 = new javax.swing.JLabel();
        txtValorChamado = new javax.swing.JTextField();
        cboStatusChamado = new javax.swing.JComboBox();
        jLabel3 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        txtPesquisarEmpreChamado = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblEmpresaChamado = new javax.swing.JTable();
        txtIdEmpreChamado = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        txtTituloChamado = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        txtDescricaoChamado = new javax.swing.JTextArea();
        jLabel8 = new javax.swing.JLabel();
        cboGrupoChamado = new javax.swing.JComboBox();
        jLabel9 = new javax.swing.JLabel();
        cboUrgenciaChamado = new javax.swing.JComboBox();
        jLabel10 = new javax.swing.JLabel();
        cboCategoriaChamado = new javax.swing.JComboBox();
        jLabel11 = new javax.swing.JLabel();
        btnAdicionarChamado = new javax.swing.JButton();
        btnPesquisarChamado = new javax.swing.JButton();
        btnAlterarChamado = new javax.swing.JButton();
        btnExcluirChamado = new javax.swing.JButton();
        btnImprimirChamado = new javax.swing.JButton();
        cboAtribuidoChamado = new javax.swing.JComboBox();
        jLabel13 = new javax.swing.JLabel();
        txtEquipamentoChamado = new javax.swing.JTextField();

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setTitle("Mega Center - Chamados");
        setToolTipText("");
        setPreferredSize(new java.awt.Dimension(630, 480));
        addInternalFrameListener(new javax.swing.event.InternalFrameListener() {
            public void internalFrameActivated(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameClosed(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameClosing(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameDeactivated(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameDeiconified(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameIconified(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameOpened(javax.swing.event.InternalFrameEvent evt) {
                formInternalFrameOpened(evt);
            }
        });

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel1.setText("Nº Chamado:");

        jLabel2.setText("Data:");

        txtIdChamado.setEditable(false);
        txtIdChamado.setEnabled(false);

        txtDataChamado.setEditable(false);
        txtDataChamado.setEnabled(false);

        buttonGroup1.add(rbtOrcamentoChamado);
        rbtOrcamentoChamado.setText("Orçamento");
        rbtOrcamentoChamado.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbtOrcamentoChamadoActionPerformed(evt);
            }
        });

        buttonGroup1.add(rbtChamado);
        rbtChamado.setText("Chamado");
        rbtChamado.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbtChamadoActionPerformed(evt);
            }
        });

        jLabel12.setText("Valor Total");

        txtValorChamado.setText("0");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGap(34, 34, 34))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(txtIdChamado)
                                .addGap(19, 19, 19)))
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addComponent(txtDataChamado)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(rbtChamado)
                            .addComponent(rbtOrcamentoChamado))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel12)
                                .addGap(0, 106, Short.MAX_VALUE))
                            .addComponent(txtValorChamado))))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtIdChamado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtDataChamado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(5, 5, 5)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel12)
                    .addComponent(rbtOrcamentoChamado))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtValorChamado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(rbtChamado))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        cboStatusChamado.setModel(new javax.swing.DefaultComboBoxModel(new String[] { " ", "Aguardando atendimento técnico", "Aguardando peças, equipamentos ou suprimentos", "Aguardando resolução dos testes", "Aguardando restabelecimento dos serviços", "Aguardando retorno do chamado", "Aguardando retorno do Usuário" }));

        jLabel3.setText("Status *");

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Empresa"));

        txtPesquisarEmpreChamado.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtPesquisarEmpreChamadoKeyReleased(evt);
            }
        });

        jLabel4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/megacenter/icones/pesquisar.png"))); // NOI18N

        jLabel5.setText("Código *");

        tblEmpresaChamado.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Código", "Nome", "Telefone"
            }
        ));
        tblEmpresaChamado.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblEmpresaChamadoMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblEmpresaChamado);

        txtIdEmpreChamado.setEditable(false);
        txtIdEmpreChamado.setEnabled(false);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(txtPesquisarEmpreChamado)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtIdEmpreChamado, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel5)
                        .addComponent(txtIdEmpreChamado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(txtPesquisarEmpreChamado)
                        .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 88, Short.MAX_VALUE))
        );

        jLabel6.setText("Titulo *");

        jLabel7.setText("Descrição *");

        txtDescricaoChamado.setColumns(20);
        txtDescricaoChamado.setRows(5);
        jScrollPane2.setViewportView(txtDescricaoChamado);

        jLabel8.setText("Grupo *");

        cboGrupoChamado.setModel(new javax.swing.DefaultComboBoxModel(new String[] { " ", "Balanças", "Banco de Dados", "Cameras", "Computadores, Monitores e Periféricos", "Elétrica", "Impressoras", "Rede e Internet", "Segurança", "Servidores", "Sistemas", "Alterdata", "Antivírus", "GPLI", "Máxima Roteirizador", "Winthor", "Caixa 2075", " " }));

        jLabel9.setText("Urgência");

        cboUrgenciaChamado.setModel(new javax.swing.DefaultComboBoxModel(new String[] { " ", "Média", "Muito Alta", "Muito Baixa" }));

        jLabel10.setText("Atribuído *");

        cboCategoriaChamado.setModel(new javax.swing.DefaultComboBoxModel(new String[] { " ", "Incidente", "Requisição", "Troca" }));

        jLabel11.setText("Categoria *");

        btnAdicionarChamado.setText("Criar chamado");
        btnAdicionarChamado.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAdicionarChamadoActionPerformed(evt);
            }
        });

        btnPesquisarChamado.setText("Consulta Chamado");
        btnPesquisarChamado.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPesquisarChamadoActionPerformed(evt);
            }
        });

        btnAlterarChamado.setText("Alterar Chamado");
        btnAlterarChamado.setEnabled(false);
        btnAlterarChamado.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAlterarChamadoActionPerformed(evt);
            }
        });

        btnExcluirChamado.setText("Excluir Chamado");
        btnExcluirChamado.setEnabled(false);
        btnExcluirChamado.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExcluirChamadoActionPerformed(evt);
            }
        });

        btnImprimirChamado.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/megacenter/icones/icon_imprimir.png"))); // NOI18N
        btnImprimirChamado.setToolTipText("Imprimir Chamado");
        btnImprimirChamado.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        btnImprimirChamado.setEnabled(false);
        btnImprimirChamado.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnImprimirChamadoActionPerformed(evt);
            }
        });

        cboAtribuidoChamado.setModel(new javax.swing.DefaultComboBoxModel(new String[] { " ", "Rodrigo Coelho", "Enio Lancone", "Alexsander Rodrigues", "José Anderson" }));

        jLabel13.setText("Equipamento *");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(btnAdicionarChamado, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(btnPesquisarChamado, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(btnAlterarChamado, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(btnExcluirChamado, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(btnImprimirChamado, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 375, Short.MAX_VALUE)
                            .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel7)
                            .addComponent(txtTituloChamado)
                            .addComponent(jLabel13)
                            .addComponent(txtEquipamentoChamado))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(cboStatusChamado, 0, 0, Short.MAX_VALUE)
                            .addComponent(cboGrupoChamado, 0, 1, Short.MAX_VALUE)
                            .addComponent(cboUrgenciaChamado, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(cboCategoriaChamado, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel8)
                                    .addComponent(jLabel9)
                                    .addComponent(jLabel3)
                                    .addComponent(jLabel11)
                                    .addComponent(jLabel10))
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addComponent(cboAtribuidoChamado, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel11)
                    .addComponent(jLabel13))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cboCategoriaChamado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtEquipamentoChamado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(jLabel6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cboGrupoChamado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtTituloChamado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(jLabel7))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(cboUrgenciaChamado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cboStatusChamado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnAdicionarChamado)
                            .addComponent(btnAlterarChamado))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnExcluirChamado)
                            .addComponent(btnPesquisarChamado)))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                            .addComponent(jLabel10)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(cboAtribuidoChamado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addComponent(btnImprimirChamado)))
                .addContainerGap(77, Short.MAX_VALUE))
        );

        setBounds(0, 0, 794, 515);
    }// </editor-fold>//GEN-END:initComponents

    private void txtPesquisarEmpreChamadoKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtPesquisarEmpreChamadoKeyReleased
        // Chamando o metodo pesquisar empresas
        pesquisar_empresa();
    }//GEN-LAST:event_txtPesquisarEmpreChamadoKeyReleased

    private void tblEmpresaChamadoMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblEmpresaChamadoMouseClicked
        // chamando o metodo preencher campos
        preencher_campos();
    }//GEN-LAST:event_tblEmpresaChamadoMouseClicked

    private void rbtOrcamentoChamadoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbtOrcamentoChamadoActionPerformed
        // atribuindo um texto a variável do tipo se o radio button estiver selecionado
        tipo = "orçamento";
    }//GEN-LAST:event_rbtOrcamentoChamadoActionPerformed

    private void rbtChamadoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbtChamadoActionPerformed
        // a linha abaxio um texto a variável do tipo se o radio button estiver selecionado
        tipo = "chamado";
    }//GEN-LAST:event_rbtChamadoActionPerformed

    private void formInternalFrameOpened(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameOpened
        // ao abrir o forme, marcar o radio button chamado
        rbtChamado.setSelected(true);
        tipo = "chamado";
    }//GEN-LAST:event_formInternalFrameOpened

    private void btnAdicionarChamadoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAdicionarChamadoActionPerformed
        // Chamando o metodo emitir chamado
        emitir_chamado();
    }//GEN-LAST:event_btnAdicionarChamadoActionPerformed

    private void btnPesquisarChamadoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPesquisarChamadoActionPerformed
        // chamando o metodo pesquisar chamado
        pesquisar_chamado();
    }//GEN-LAST:event_btnPesquisarChamadoActionPerformed

    private void btnAlterarChamadoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAlterarChamadoActionPerformed
        // chamando o metodo alterar chamado
        alterar_chamado();
    }//GEN-LAST:event_btnAlterarChamadoActionPerformed

    private void btnExcluirChamadoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExcluirChamadoActionPerformed
        // chmando o metodo para excluir o chamado
        excluir_chamado();
    }//GEN-LAST:event_btnExcluirChamadoActionPerformed

    private void btnImprimirChamadoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnImprimirChamadoActionPerformed
        // chamando o metodo imprimi chamado
        imprimir_chamado();
    }//GEN-LAST:event_btnImprimirChamadoActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAdicionarChamado;
    private javax.swing.JButton btnAlterarChamado;
    private javax.swing.JButton btnExcluirChamado;
    private javax.swing.JButton btnImprimirChamado;
    private javax.swing.JButton btnPesquisarChamado;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JComboBox cboAtribuidoChamado;
    private javax.swing.JComboBox cboCategoriaChamado;
    private javax.swing.JComboBox cboGrupoChamado;
    private javax.swing.JComboBox cboStatusChamado;
    private javax.swing.JComboBox cboUrgenciaChamado;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JRadioButton rbtChamado;
    private javax.swing.JRadioButton rbtOrcamentoChamado;
    private javax.swing.JTable tblEmpresaChamado;
    private javax.swing.JTextField txtDataChamado;
    private javax.swing.JTextArea txtDescricaoChamado;
    private javax.swing.JTextField txtEquipamentoChamado;
    private javax.swing.JTextField txtIdChamado;
    private javax.swing.JTextField txtIdEmpreChamado;
    private javax.swing.JTextField txtPesquisarEmpreChamado;
    private javax.swing.JTextField txtTituloChamado;
    private javax.swing.JTextField txtValorChamado;
    // End of variables declaration//GEN-END:variables
}
