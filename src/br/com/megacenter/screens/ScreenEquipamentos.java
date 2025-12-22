/*
 * The MIT License
 *
 * Copyright 2025 JANDERSON.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package br.com.megacenter.screens;

import br.com.megacenter.dal.ModuloConexao;
import java.awt.Image;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.RowFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import net.proteanit.sql.DbUtils;

// Imports do JFreeChart
/**
 *
 * @author JANDERSON
 */
public class ScreenEquipamentos extends javax.swing.JInternalFrame {

    Connection conexao = null;
    PreparedStatement pst = null;
    ResultSet rs = null;

    /**
     * Creates new form ScreenEquipamentos
     */
    public ScreenEquipamentos() {
        initComponents();
        conexao = ModuloConexao.conector();
        atualizarTotalEquipamentos();
        exibir_todos_equipamentos_dashboard();
        carregarEmpresasNoCombo();// chamando o metodo carregar empresas
        carregarFilialNoCombo();
        carregarFiltroFilialNoCombo();
        carregarFiltroTipolNoCombo();
        carregarSetoresNoCombo();

        sorter = new TableRowSorter<>(tblEquipamentos.getModel());
        tblEquipamentos.setRowSorter(sorter);

        // Adiciona um listener no campo de pesquisa para filtrar dinamicamente
        txtPesquisaEqui.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                aplicarFiltro();
            }

            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                aplicarFiltro();
            }

            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                aplicarFiltro();
            }
        });

        bloquearEdicaoTabela();

    }

    // Método auxiliar para evitar NullPointerException
    private String valorOuVazio(Object valor) {
        return (valor == null) ? "" : valor.toString();
    }

    // Criando o método para aplicar filtros tipo Exel na tabela
    private TableRowSorter<TableModel> sorter;

    /**
     * Método para atualizar a quantidade total de equipamentos.
     */
    public void atualizarTotalEquipamentos() {
        int total = Dashboard.getTotalEquipamentos();
        lblTotalEquipamentos.setText(" " + total);

        String quantidadePorTipo = Dashboard.getQuantidadePorTipo();
        txtQuantidadePorTipo.setText(quantidadePorTipo);

        double valorTotal = Dashboard.getValorTotalEquipamentos();
        lblValorTotal.setText(" R$ " + String.format("%.2f", valorTotal));

        // == == == CARDS POR STATUS ======
        lblAtivos.setText(
                String.valueOf(Dashboard.getTotalPorStatus("Ativo"))
        );

        lblDevolvidos.setText(
                String.valueOf(Dashboard.getTotalPorStatus("Devolvido"))
        );

        lblPendentes.setText(
                String.valueOf(Dashboard.getTotalPorStatus("Pendente"))
        );

        lblReservas.setText(
                String.valueOf(Dashboard.getTotalPorStatus("Reserva"))
        );
    }

    private void aplicarFiltro() {
        String texto = txtPesquisaEqui.getText();
        if (texto.trim().length() == 0) {
            sorter.setRowFilter(null); // Mostra todos os registros
        } else {
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + texto)); // Filtro insensível a maiúsculas/minúsculas
        }
    }

    private void aplicarFiltroDescricao() {
        String texto = txtFiltroDescricaoEqui.getText();
        if (texto.trim().length() == 0) {
            sorter.setRowFilter(null); // Mostra todos os registros
        } else {
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + texto, 9)); // Filtro insensível a maiúsculas/minúsculas
        }
    }

    ///////////////////////////////////PREENCHE AS INFORMAÇÕES DA TABELA/////////////////////////////////
    // criando o metodo para pesquisar os equipamentos pelo número da etiqueta
    private void pesquisar_equipamento() {
        String sql = "Select idequipamento as Código, codigo_filial as Filial, etiqueta_equipamento as Etiqueta, data_cadastrado as Data, tipo as Tipo, marca as Marca, setor as Setor"
                + ", funcionario as Funcionário, valor as Valor, foto_equipamento as Imagem, descricao as Descrição, condicoes_equipamento as Condições, status as Status,"
                + "data_saida as 'Data de Saída', quantidade as Quantidade from equipamentos where etiqueta_equipamento "
                + "like ?";
        try {
            pst = conexao.prepareStatement(sql);
            pst.setString(1, txtPesquisaEqui.getText() + "%");
            rs = pst.executeQuery();
            tblEquipamentos.setModel(DbUtils.resultSetToTableModel(rs));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }

    // Método para exibir todos os equipamentos cadastrados na tabela
    private void exibir_todos_equipamentos_dashboard() {
        String sql = "SELECT idequipamento as Código, codigo_filial as Filial, etiqueta_equipamento as Etiqueta, tipo as Tipo, descricao as Descrição, "
                + "setor as Setor, funcionario as Funcionário, valor as Valor, quantidade as Quantidade, codigo_empresa as Fornecedor, data_cadastrado as 'Data da Entrada', "
                + "data_saida as 'Data de Saída', status as Status, marca as Marca, condicoes_equipamento as Condições FROM equipamentos";
        try {
            pst = conexao.prepareStatement(sql);
            rs = pst.executeQuery();
            tblEquipamentos.setModel(DbUtils.resultSetToTableModel(rs));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////PESQUISA////////////////////////////////////////////////////////
    // criando o metodo para pesquisar os equipamentos pela Etiqueta do equipamento e exibir na tabela
    private void exibir_pesquisar_equipamento() {
        String sql = "Select idequipamento as Código, codigo_filial as Filial, etiqueta_equipamento as Etiqueta, tipo as Tipo, descricao as Descrição, "
                + "setor as Setor, funcionario as Funcionário, valor as Valor, quantidade as Quantidade, codigo_empresa as Fornecedor, data_cadastrado as 'Data da Entrada', "
                + "data_saida as 'Data de Saída', status as Status, marca as Marca, condicoes_equipamento as Condições from equipamentos where etiqueta_equipamento like ?";
        try {
            pst = conexao.prepareStatement(sql);
            pst.setString(1, txtPesquisaEqui.getText() + "%");
            rs = pst.executeQuery();
            tblEquipamentos3.setModel(DbUtils.resultSetToTableModel(rs));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }

    // criando o metodo para pesquisar os equipamentos pelo nome do equipamento
    private void filtro_pesquisar_equipamento() {
        String sql = "Select idequipamento as Código, codigo_filial as Filial, etiqueta_equipamento as Etiqueta, tipo as Tipo, descricao as Descrição, "
                + "setor as Setor, funcionario as Funcionário, valor as Valor, quantidade as Quantidade, codigo_empresa as Fornecedor, data_cadastrado as 'Data da Entrada', "
                + "data_saida as 'Data de Saída', status as Status, marca as Marca, condicoes_equipamento as Condições from equipamentos where descricao like ?";

        try {
            pst = conexao.prepareStatement(sql);
            pst.setString(1, "%" + txtFiltroDescricaoEqui.getText() + "%"); // Busca em qualquer parte
            rs = pst.executeQuery();
            tblEquipamentos3.setModel(DbUtils.resultSetToTableModel(rs));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Método para deixar a tabela somente para visualização
    private void bloquearEdicaoTabela() {
        // Impede edição de células
        tblEquipamentos.setDefaultEditor(Object.class, null);

        // Impede que o usuário arraste e mude a posição das colunas
        tblEquipamentos.getTableHeader().setReorderingAllowed(false);

        // Impede o redimensionamento das colunas (opcional, se quiser também travar)
        tblEquipamentos.getTableHeader().setResizingAllowed(false);
    }

    ///////////Criando o metodo para preencher as culas da tabela do dashboard de acordo com o banco de dados/////////
    private void preencher_campos() {
        try {
            conexao = ModuloConexao.conector(); // Garante que está conectado ao banco

            int preencher = tblEquipamentos.getSelectedRow();
            if (preencher == -1) {
                return; // Sai sem fazer nada
            }

            // Preenchendo os campos de texto com tratamento para valores null
            txtIdEqui.setText(valorOuVazio(tblEquipamentos.getModel().getValueAt(preencher, 0)));
            cboFilialEqui.setSelectedItem(valorOuVazio(tblEquipamentos.getModel().getValueAt(preencher, 1)));
            txtEtiquetaEqui.setText(valorOuVazio(tblEquipamentos.getModel().getValueAt(preencher, 2)));
            cboTipoEqui.setSelectedItem(valorOuVazio(tblEquipamentos.getModel().getValueAt(preencher, 3)));
            txtDescricaoEqui.setText(valorOuVazio(tblEquipamentos.getModel().getValueAt(preencher, 4)));
            cboSetorEqui.setSelectedItem(valorOuVazio(tblEquipamentos.getModel().getValueAt(preencher, 5)));
            txtFuncionarioEqui.setText(valorOuVazio(tblEquipamentos.getModel().getValueAt(preencher, 6)));
            txtValorEqui.setText(valorOuVazio(tblEquipamentos.getModel().getValueAt(preencher, 7)));
            txtQuantidadeEqui.setText(valorOuVazio(tblEquipamentos.getModel().getValueAt(preencher, 8)));
            cboFornecedorEqui.setSelectedItem(valorOuVazio(tblEquipamentos.getModel().getValueAt(preencher, 9)));
            // DATA: trate corretamente conforme o tipo vindo do model
            Object valorData = tblEquipamentos.getModel().getValueAt(preencher, 10);
            if (valorData instanceof java.util.Date) {
                // Timestamp também é instanceOf java.util.Date
                dtEntradaEqui.setDate((java.util.Date) valorData);
            } else if (valorData instanceof String) {
                String s = ((String) valorData).trim();
                if (!s.isEmpty()) {
                    try {
                        // ajustar o padrão conforme o formato da String (ex: "yyyy-MM-dd" ou "dd/MM/yyyy")
                        java.util.Date dataConvertida = new java.text.SimpleDateFormat("yyyy-MM-dd").parse(s);
                        dtEntradaEqui.setDate(dataConvertida);
                    } catch (java.text.ParseException ex) {
                        dtEntradaEqui.setDate(null); // ou tratar de outra forma
                    }
                } else {
                    dtEntradaEqui.setDate(null);
                }
            } else {
                dtEntradaEqui.setDate(null);
            }

            // DATA: trate corretamente conforme o tipo vindo do model
            Object valorDataa = tblEquipamentos.getModel().getValueAt(preencher, 11);
            if (valorDataa instanceof java.util.Date) {
                // Timestamp também é instanceOf java.util.Date
                dtSaidaEqui.setDate((java.util.Date) valorDataa);
            } else if (valorDataa instanceof String) {
                String s = ((String) valorDataa).trim();
                if (!s.isEmpty()) {
                    try {
                        // ajustar o padrão conforme o formato da String (ex: "yyyy-MM-dd" ou "dd/MM/yyyy")
                        java.util.Date dataConvertida = new java.text.SimpleDateFormat("yyyy-MM-dd").parse(s);
                        dtSaidaEqui.setDate(dataConvertida);
                    } catch (java.text.ParseException ex) {
                        dtSaidaEqui.setDate(null); // ou tratar de outra forma
                    }
                } else {
                    dtSaidaEqui.setDate(null);
                }
            }

            cboStatusEqui.setSelectedItem(valorOuVazio(tblEquipamentos.getModel().getValueAt(preencher, 12)));
            txtMarcaEqui.setText(valorOuVazio(tblEquipamentos.getModel().getValueAt(preencher, 13)));
            txtCondEqui.setText(valorOuVazio(tblEquipamentos.getModel().getValueAt(preencher, 14)));

            // Carregando imagem a partir do banco
            String idStr = valorOuVazio(tblEquipamentos.getModel().getValueAt(preencher, 0));
            if (!idStr.isEmpty()) {
                int id = Integer.parseInt(idStr);
                String sql = "SELECT foto_equipamento FROM equipamentos WHERE idequipamento = ?";
                PreparedStatement pstImg = conexao.prepareStatement(sql);
                pstImg.setInt(1, id);
                ResultSet rs = pstImg.executeQuery();

                if (rs.next()) {
                    byte[] imgBytes = rs.getBytes("foto_equipamento");
                    if (imgBytes != null && imgBytes.length > 0) {
                        ImageIcon imageIcon = new ImageIcon(imgBytes);
                        Image img = imageIcon.getImage().getScaledInstance(
                                lblFotoPerfilEqui.getWidth(),
                                lblFotoPerfilEqui.getHeight(),
                                Image.SCALE_SMOOTH
                        );
                        lblFotoPerfilEqui.setIcon(new ImageIcon(img));
                        lblFotoPerfilEqui.setText("");
                    } else {
                        lblFotoPerfilEqui.setIcon(null);
                        lblFotoPerfilEqui.setText("Sem imagem");
                    }
                }

                rs.close();
                pstImg.close();
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Erro ao preencher os campos: " + e.getMessage());
        }
    }

    //////////////Criando o metodo para exibir as informações nos campos da tabela de cadastro////////////////////////////////////
    private void exibir_campos() {
        try {
            conexao = ModuloConexao.conector(); // Garante que está conectado ao banco

            int preencher = tblEquipamentos3.getSelectedRow();
            if (preencher == -1) {
                return;
            }

            // Preenchendo os campos de texto com tratamento para valores null
            txtIdEqui.setText(valorOuVazio(tblEquipamentos3.getModel().getValueAt(preencher, 0)));
            cboFilialEqui.setSelectedItem(valorOuVazio(tblEquipamentos3.getModel().getValueAt(preencher, 1)));
            txtEtiquetaEqui.setText(valorOuVazio(tblEquipamentos3.getModel().getValueAt(preencher, 2)));
            cboTipoEqui.setSelectedItem(valorOuVazio(tblEquipamentos3.getModel().getValueAt(preencher, 3)));
            txtDescricaoEqui.setText(valorOuVazio(tblEquipamentos3.getModel().getValueAt(preencher, 4)));
            cboSetorEqui.setSelectedItem(valorOuVazio(tblEquipamentos3.getModel().getValueAt(preencher, 5)));
            txtFuncionarioEqui.setText(valorOuVazio(tblEquipamentos3.getModel().getValueAt(preencher, 6)));
            txtValorEqui.setText(valorOuVazio(tblEquipamentos3.getModel().getValueAt(preencher, 7)));
            txtQuantidadeEqui.setText(valorOuVazio(tblEquipamentos3.getModel().getValueAt(preencher, 8)));
            cboFornecedorEqui.setSelectedItem(valorOuVazio(tblEquipamentos3.getModel().getValueAt(preencher, 9)));
            // DATA: trate corretamente conforme o tipo vindo do model
            Object valorData = tblEquipamentos3.getModel().getValueAt(preencher, 10);
            if (valorData instanceof java.util.Date) {
                // Timestamp também é instanceOf java.util.Date
                dtEntradaEqui.setDate((java.util.Date) valorData);
            } else if (valorData instanceof String) {
                String s = ((String) valorData).trim();
                if (!s.isEmpty()) {
                    try {
                        // ajustar o padrão conforme o formato da String (ex: "yyyy-MM-dd" ou "dd/MM/yyyy")
                        java.util.Date dataConvertida = new java.text.SimpleDateFormat("yyyy-MM-dd").parse(s);
                        dtEntradaEqui.setDate(dataConvertida);
                    } catch (java.text.ParseException ex) {
                        dtEntradaEqui.setDate(null); // ou tratar de outra forma
                    }
                } else {
                    dtEntradaEqui.setDate(null);
                }
            } else {
                dtEntradaEqui.setDate(null);
            }

            // DATA: trate corretamente conforme o tipo vindo do model
            Object valorDataa = tblEquipamentos3.getModel().getValueAt(preencher, 11);
            if (valorDataa instanceof java.util.Date) {
                // Timestamp também é instanceOf java.util.Date
                dtSaidaEqui.setDate((java.util.Date) valorDataa);
            } else if (valorDataa instanceof String) {
                String s = ((String) valorDataa).trim();
                if (!s.isEmpty()) {
                    try {
                        // ajustar o padrão conforme o formato da String (ex: "yyyy-MM-dd" ou "dd/MM/yyyy")
                        java.util.Date dataConvertida = new java.text.SimpleDateFormat("yyyy-MM-dd").parse(s);
                        dtSaidaEqui.setDate(dataConvertida);
                    } catch (java.text.ParseException ex) {
                        dtSaidaEqui.setDate(null); // ou tratar de outra forma
                    }
                } else {
                    dtSaidaEqui.setDate(null);
                }
            }

            cboStatusEqui.setSelectedItem(valorOuVazio(tblEquipamentos3.getModel().getValueAt(preencher, 12)));
            txtMarcaEqui.setText(valorOuVazio(tblEquipamentos3.getModel().getValueAt(preencher, 13)));
            txtCondEqui.setText(valorOuVazio(tblEquipamentos3.getModel().getValueAt(preencher, 14)));

            // Carregando imagem a partir do banco
            String idStr = valorOuVazio(tblEquipamentos3.getModel().getValueAt(preencher, 0));
            if (!idStr.isEmpty()) {
                int id = Integer.parseInt(idStr);
                String sql = "SELECT foto_equipamento FROM equipamentos WHERE idequipamento = ?";
                PreparedStatement pstImg = conexao.prepareStatement(sql);
                pstImg.setInt(1, id);
                ResultSet rs = pstImg.executeQuery();

                if (rs.next()) {
                    byte[] imgBytes = rs.getBytes("foto_equipamento");
                    if (imgBytes != null && imgBytes.length > 0) {
                        ImageIcon imageIcon = new ImageIcon(imgBytes);
                        Image img = imageIcon.getImage().getScaledInstance(
                                lblFotoPerfilEqui.getWidth(),
                                lblFotoPerfilEqui.getHeight(),
                                Image.SCALE_SMOOTH
                        );
                        lblFotoPerfilEqui.setIcon(new ImageIcon(img));
                        lblFotoPerfilEqui.setText("");
                    } else {
                        lblFotoPerfilEqui.setIcon(null);
                        lblFotoPerfilEqui.setText("Sem imagem");
                    }
                }

                rs.close();
                pstImg.close();
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Erro ao preencher os campos: " + e.getMessage());
        }
    }

    private void cadastrar_equipamento() {

        String sql = "INSERT INTO equipamentos "
                + "(codigo_filial, etiqueta_equipamento, tipo, descricao, setor, funcionario, valor, quantidade, "
                + "codigo_empresa, data_cadastrado, data_saida, status, marca, condicoes_equipamento, foto_equipamento) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        if (txtEtiquetaEqui.getText().trim().isEmpty()
                || txtFuncionarioEqui.getText().trim().isEmpty()
                || cboSetorEqui.getSelectedItem() == null
                || cboTipoEqui.getSelectedItem() == null
                || dtEntradaEqui.getDate() == null) {

            JOptionPane.showMessageDialog(null, "Preencha todos os campos obrigatórios.");
            return;
        }

        File imagem = new File(txtDiretorioImagemEqui.getText());
        if (!imagem.exists()) {
            JOptionPane.showMessageDialog(null, "Imagem não encontrada.");
            return;
        }

        try (PreparedStatement pst = conexao.prepareStatement(sql);
                FileInputStream fis = new FileInputStream(imagem)) {

            pst.setInt(1, Integer.parseInt(cboFilialEqui.getSelectedItem().toString()));
            pst.setInt(2, Integer.parseInt(txtEtiquetaEqui.getText()));
            pst.setString(3, cboTipoEqui.getSelectedItem().toString());
            pst.setString(4, txtDescricaoEqui.getText());
            pst.setString(5, cboSetorEqui.getSelectedItem().toString());
            pst.setString(6, txtFuncionarioEqui.getText());
            pst.setString(7, txtValorEqui.getText().replace(",", "."));
            pst.setString(8, txtQuantidadeEqui.getText());
            pst.setString(9, cboFornecedorEqui.getSelectedItem().toString());

            pst.setTimestamp(10, new java.sql.Timestamp(dtEntradaEqui.getDate().getTime()));

            if (dtSaidaEqui.getDate() != null) {
                pst.setTimestamp(11, new java.sql.Timestamp(dtSaidaEqui.getDate().getTime()));
            } else {
                pst.setNull(11, java.sql.Types.TIMESTAMP);
            }

            pst.setString(12, cboStatusEqui.getSelectedItem().toString());
            pst.setString(13, txtMarcaEqui.getText());
            pst.setString(14, txtCondEqui.getText());
            pst.setBinaryStream(15, fis, imagem.length());

            pst.executeUpdate();

            JOptionPane.showMessageDialog(null, "Equipamento cadastrado com sucesso!");
            limpar_campos(); //chamando o metodo
            exibir_todos_equipamentos_dashboard(); //chamando o metodo
            atualizarTotalEquipamentos(); //chamando o metodo

            // ===============================
            // 🔄 ATUALIZA O DASHBOARD EM TEMPO REAL
            // ===============================
            java.awt.Window window
                    = javax.swing.SwingUtilities.getWindowAncestor(this);

            if (window instanceof ScreenPrincipal) {

                ScreenPrincipal principal = (ScreenPrincipal) window;

                ScreenDashboard dashboard = principal.getDashboard();

                if (dashboard != null && !dashboard.isClosed()) {
                    dashboard.atualizarDashboardCompleto();
                }
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro SQL: " + e.getMessage());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Erro inesperado: " + e.toString());
            e.printStackTrace();
        }
    }

    // Criando o método para alterar um equipamento
    private void alterar_equipamento() {
        String sql = "UPDATE equipamentos SET etiqueta_equipamento=?, tipo=?, marca=?, setor=?, funcionario=?, valor=?, foto_equipamento=?, "
                + "descricao=?, condicoes_equipamento=?, status=?, id_filial=?, data_saida=?, quantidade=? WHERE idequipamento=?";

        FileInputStream fis = null;

        try {
            // Validação dos campos obrigatórios
            if (txtEtiquetaEqui.getText().isEmpty() || txtFuncionarioEqui.getText().isEmpty()
                    || cboSetorEqui.getSelectedItem().equals(" ") || cboTipoEqui.getSelectedItem().equals(" ")) {
                JOptionPane.showMessageDialog(null, "Por favor, preencha todos os campos obrigatórios.");
                return;
            }

            // Verifica se o arquivo de imagem existe
            File imagem = new File(txtDiretorioImagemEqui.getText());
            if (!imagem.exists()) {
                JOptionPane.showMessageDialog(null, "Imagem não encontrada no caminho informado.");
                return;
            }

            // Preparando a conexão
            pst = conexao.prepareStatement(sql);
            pst.setString(1, txtEtiquetaEqui.getText());
            pst.setString(2, cboTipoEqui.getSelectedItem().toString());
            pst.setString(3, txtMarcaEqui.getText());
            pst.setString(4, cboSetorEqui.getSelectedItem().toString());
            pst.setString(5, txtFuncionarioEqui.getText());
            pst.setString(6, txtValorEqui.getText().replace(",", ".")); // Valor numérico com "." decimal

            // Lendo a imagem
            fis = new FileInputStream(imagem);
            pst.setBinaryStream(7, fis, (int) imagem.length());

            pst.setString(8, txtDescricaoEqui.getText());
            pst.setString(9, txtCondEqui.getText()); // condicoes_equipamento
            pst.setString(10, cboStatusEqui.getSelectedItem().toString()); // status
            pst.setString(11, txtIdEqui.getText());         // idequipamento
            pst.setString(12, cboFilialEqui.getSelectedItem().toString());
            pst.setString(13, txtQuantidadeEqui.getText());

            int atualizado = pst.executeUpdate();
            if (atualizado > 0) {
                JOptionPane.showMessageDialog(null, "Equipamento atualizado com sucesso!");
                limpar_campos(); // Limpando os campos
                exibir_todos_equipamentos_dashboard();
                atualizarTotalEquipamentos();
            }

        } catch (FileNotFoundException fnfe) {
            JOptionPane.showMessageDialog(null, "Arquivo de imagem não encontrado: " + fnfe.getMessage());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Erro ao atualizar: " + e);
        } finally {
            try {
                if (fis != null) {
                    fis.close();
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Erro ao fechar o arquivo de imagem: " + e);
            }
        }
    }

    //criando o método para excluir um Equipamento
    private void excluir_equipamento() {
        int confirma = JOptionPane.showConfirmDialog(null, "Você tem certeza que deseja "
                + "excluir este equipamento?", "Atenção", JOptionPane.YES_NO_OPTION);
        if (confirma == JOptionPane.YES_OPTION) {
            String sql = "delete from equipamentos where idequipamento = ?";
            try {
                pst = conexao.prepareStatement(sql);
                pst.setString(1, txtIdEqui.getText());
                int excluido = pst.executeUpdate();
                if (excluido > 0) {
                    JOptionPane.showMessageDialog(null, "Equipamento excluido com sucesso!");
                    limpar_campos(); // chamando o metodo limpando os campos
                    exibir_todos_equipamentos_dashboard();
                    atualizarTotalEquipamentos();
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, e);
            }
        }
    }

    // BOTÃO SALVAR ALTERAÇÕES
    private void salvarEquipamento() {

        String sql = "UPDATE equipamentos SET "
                + "etiqueta_equipamento=?, "
                + "data_cadastrado=?, "
                + "data_saida=?, "
                + "tipo=?, "
                + "marca=?, "
                + "setor=?, "
                + "funcionario=?, "
                + "valor=?, "
                + "condicoes_equipamento=?, "
                + "status=?, "
                + "descricao=? "
                + "WHERE idequipamento=?";

        try {
            pst = conexao.prepareStatement(sql);

            // 1 - etiqueta
            pst.setString(1, txtEtiquetaEqui.getText());

            // 2 - data de entrada
            java.util.Date entrada = dtEntradaEqui.getDate();
            if (entrada != null) {
                pst.setTimestamp(2, new java.sql.Timestamp(entrada.getTime()));
            } else {
                pst.setNull(2, java.sql.Types.TIMESTAMP);
            }

            // 3 - data de saída
            java.util.Date saida = dtSaidaEqui.getDate();
            if (saida != null) {
                pst.setTimestamp(3, new java.sql.Timestamp(saida.getTime()));
            } else {
                pst.setNull(3, java.sql.Types.TIMESTAMP);
            }

            pst.setString(4, cboTipoEqui.getSelectedItem().toString());
            pst.setString(5, txtMarcaEqui.getText());
            pst.setString(6, cboSetorEqui.getSelectedItem().toString());
            pst.setString(7, txtFuncionarioEqui.getText());
            String valorTxt = txtValorEqui.getText().replace(",", ".");
            pst.setDouble(8, Double.parseDouble(valorTxt));
            pst.setString(9, txtCondEqui.getText());
            pst.setString(10, cboStatusEqui.getSelectedItem().toString());
            pst.setString(11, txtDescricaoEqui.getText());
            pst.setInt(12, Integer.parseInt(txtIdEqui.getText()));

            // Executa atualização
            int atualizado = pst.executeUpdate();

            if (atualizado > 0) {
                JOptionPane.showMessageDialog(null, "Equipamento atualizado com sucesso!");
                exibir_todos_equipamentos_dashboard();
                atualizarTotalEquipamentos();
                limpar_campos();

            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Erro ao salvar: " + e);
        }
    }

    // criando o metodo para alterar a imagem
    private void alterarImagemEquipamento() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Selecionar nova imagem");

        int resultado = chooser.showOpenDialog(null);

        if (resultado == JFileChooser.APPROVE_OPTION) {
            File arquivo = chooser.getSelectedFile();

            try {
                // LER IMAGEM EM BYTES MANUALMENTE (compatível com Java 7/8)
                FileInputStream fis = new FileInputStream(arquivo);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int bytesLidos;

                while ((bytesLidos = fis.read(buffer)) != -1) {
                    baos.write(buffer, 0, bytesLidos);
                }

                byte[] imagemBytes = baos.toByteArray();
                fis.close();
                baos.close();

                // ATUALIZAR NO BANCO
                String sql = "UPDATE equipamentos SET foto_equipamento=? WHERE idequipamento=?";
                pst = conexao.prepareStatement(sql);

                pst.setBytes(1, imagemBytes);
                pst.setInt(2, Integer.parseInt(txtIdEqui.getText()));

                int atualizou = pst.executeUpdate();

                if (atualizou > 0) {

                    // EXIBIR A IMAGEM NO LABEL
                    ImageIcon icone = new ImageIcon(imagemBytes);
                    Image img = icone.getImage().getScaledInstance(
                            lblFotoPerfilEqui.getWidth(),
                            lblFotoPerfilEqui.getHeight(),
                            Image.SCALE_SMOOTH
                    );

                    lblFotoPerfilEqui.setIcon(new ImageIcon(img));

                    JOptionPane.showMessageDialog(null, "Imagem atualizada com sucesso!");
                }

            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Erro ao alterar imagem: " + e);
            }
        }
    }

    // criando o metodo para exlcuir a imagem
    private void excluirImagemEquipamento() {
        String sql = "UPDATE equipamentos SET foto_equipamento = NULL WHERE idequipamento = ?";

        try {
            pst = conexao.prepareStatement(sql);
            pst.setInt(1, Integer.parseInt(txtIdEqui.getText()));

            int apagou = pst.executeUpdate();

            if (apagou > 0) {
                lblFotoPerfilEqui.setIcon(null); // limpa na tela
                JOptionPane.showMessageDialog(null, "Imagem removida com sucesso!");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Erro ao excluir imagem: " + e);
        }
    }

    // criando o metodo para carregar os setores no combobox setor
    private void carregarSetoresNoCombo() {
        String sql = "SELECT descricao FROM setores ORDER BY descricao ASC";

        try {
            pst = conexao.prepareStatement(sql);
            rs = pst.executeQuery();

            cboSetorEqui.removeAllItems(); // Limpa opções atuais
            cboSetorEqui.addItem("Selecione"); // opção padrão

            while (rs.next()) {
                String descricaoSetor = rs.getString("descricao");
                cboSetorEqui.addItem(descricaoSetor);
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Erro ao carregar todos os setores: " + e.getMessage());
        }
    }

    // criando o metodo para carregar as empresas no combobox de fornecedor
    private void carregarEmpresasNoCombo() {
        String sql = "SELECT razao_social FROM empresas ORDER BY razao_social ASC";

        try {
            pst = conexao.prepareStatement(sql);
            rs = pst.executeQuery();

            cboFornecedorEqui.removeAllItems(); // Limpa opções atuais
            cboFornecedorEqui.addItem("Selecione"); // opção padrão

            while (rs.next()) {
                String nomeEmpresa = rs.getString("razao_social");
                cboFornecedorEqui.addItem(nomeEmpresa);
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Erro ao carregar empresas: " + e.getMessage());
        }
    }

    // criando o metodo para carregar as filias no combobox de filial na tela de cadastro de equipamentos
    private void carregarFilialNoCombo() {

        String sql = "SELECT codigo_filial FROM filial ORDER BY codigo_filial ASC";

        try {
            pst = conexao.prepareStatement(sql);
            rs = pst.executeQuery();

            cboFilialEqui.removeAllItems();     // Limpa o ComboBox
            cboFilialEqui.addItem(""); // Opção padrão

            while (rs.next()) {
                cboFilialEqui.addItem(rs.getString("codigo_filial"));
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Erro ao carregar as filiais.\n" + e.getMessage(),
                    "Erro",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // criando o metodo para carregar as filias do filtro no combobox de filial
    private void carregarFiltroFilialNoCombo() {

        String sql = "SELECT codigo_filial FROM filial ORDER BY codigo_filial ASC";

        try {
            pst = conexao.prepareStatement(sql);
            rs = pst.executeQuery();

            cboFiltroFilialEqui.removeAllItems();     // Limpa o ComboBox
            cboFiltroFilialEqui.addItem(""); // Opção padrão

            while (rs.next()) {
                cboFiltroFilialEqui.addItem(rs.getString("codigo_filial"));
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Erro ao carregar as filiais.\n" + e.getMessage(),
                    "Erro",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // criando o metodo para carregar as filias do filtro no combobox de filial
    private void carregarFiltroTipolNoCombo() {

        String sql = "SELECT descricao FROM tipo ORDER BY descricao ASC";

        try {
            pst = conexao.prepareStatement(sql);
            rs = pst.executeQuery();

            cboFiltroTipoEqui.removeAllItems(); // Limpa o ComboBox
            cboFiltroTipoEqui.addItem(""); // Opção padrão

            while (rs.next()) {
                cboFiltroTipoEqui.addItem(rs.getString("descricao"));
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Erro ao carregar os tipos de equipamentos.\n" + e.getMessage(),
                    "Erro",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // limpar campos e habilitar os botões e gerenciar os botões
    private void limpar_campos() {
        // limpando os campos
        txtIdEqui.setText(null);
        txtEtiquetaEqui.setText(null);
        dtEntradaEqui.setDate(null);
        txtMarcaEqui.setText(null);
        cboSetorEqui.setSelectedItem(" ");
        cboTipoEqui.setSelectedItem(" ");
        txtValorEqui.setText(null);
        txtFuncionarioEqui.setText(null);
        // a linha abaixo limpa a foto do equipamento
        lblFotoPerfilEqui.setIcon(null);
        txtDescricaoEqui.setText(null);
        txtCondEqui.setText(null);
        cboStatusEqui.setSelectedItem(" ");
        //a linha abaixo limpa o campo do diretório da imagem
        txtDiretorioImagemEqui.setText(null);
        ((DefaultTableModel) tblEquipamentos.getModel()).setRowCount(0); // limpando os dados da tabela
        dtSaidaEqui.setDate(null);
        //habilitando novamente os objetos
        //btnCadastrarEqui.setEnabled(true);
        //txtIdEqui.setEnabled(true);
        // desabilitar os botões
        //btnAtualizarEqui.setEnabled(false);
        //btnExcluirEqui.setEnabled(false);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTextField1 = new javax.swing.JTextField();
        jLabel17 = new javax.swing.JLabel();
        jTabbedPane2 = new javax.swing.JTabbedPane();
        jPanel6 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblEquipamentos = new javax.swing.JTable();
        jPanel11 = new javax.swing.JPanel();
        jPanel8 = new javax.swing.JPanel();
        lblDevolvidos = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        jLabel14 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        lblValorTotal = new javax.swing.JLabel();
        jPanel12 = new javax.swing.JPanel();
        ValorTotalEquipamentos = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        lblTotalEquipamentos = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        jPanel7 = new javax.swing.JPanel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jPanel13 = new javax.swing.JPanel();
        jPanel14 = new javax.swing.JPanel();
        jLabel15 = new javax.swing.JLabel();
        lblPendentes = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        jPanel15 = new javax.swing.JPanel();
        lblAtivos = new javax.swing.JLabel();
        jPanel16 = new javax.swing.JPanel();
        jLabel35 = new javax.swing.JLabel();
        jLabel36 = new javax.swing.JLabel();
        jPanel17 = new javax.swing.JPanel();
        lblReservas = new javax.swing.JLabel();
        jLabel33 = new javax.swing.JLabel();
        jLabel34 = new javax.swing.JLabel();
        txtQuantidadePorTipo = new javax.swing.JTextField();
        jPanel9 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        btnCadastrarEqui = new javax.swing.JButton();
        btnAtualizarEqui = new javax.swing.JButton();
        btnConsultarEqui = new javax.swing.JButton();
        btnSalvarEqui = new javax.swing.JButton();
        btnExcluirEqui = new javax.swing.JButton();
        btnCancelar = new javax.swing.JButton();
        txtDiretorioImagemEqui = new javax.swing.JTextField();
        jPanel1 = new javax.swing.JPanel();
        jLabel19 = new javax.swing.JLabel();
        txtPesquisaEqui = new javax.swing.JTextField();
        jLabel18 = new javax.swing.JLabel();
        cboFiltroFilialEqui = new javax.swing.JComboBox();
        txtFiltroDescricaoEqui = new javax.swing.JTextField();
        cboFiltroTipoEqui = new javax.swing.JComboBox();
        jLabel16 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        cboFiltroUsuarioEqui = new javax.swing.JComboBox();
        jLabel22 = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        tblEquipamentos3 = new javax.swing.JTable();
        bntBuscarEqui = new javax.swing.JButton();
        jPanel10 = new javax.swing.JPanel();
        cboSetorEqui = new javax.swing.JComboBox<String>();
        cboTipoEqui = new javax.swing.JComboBox<String>();
        cboStatusEqui = new javax.swing.JComboBox();
        cboFornecedorEqui = new javax.swing.JComboBox();
        jLabel32 = new javax.swing.JLabel();
        jLabel30 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        dtEntradaEqui = new com.toedter.calendar.JDateChooser();
        jLabel11 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        dtSaidaEqui = new com.toedter.calendar.JDateChooser();
        txtMarcaEqui = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        txtQuantidadeEqui = new javax.swing.JTextField();
        txtValorEqui = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        txtIdEqui = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        jLabel31 = new javax.swing.JLabel();
        cboFilialEqui = new javax.swing.JComboBox();
        txtEtiquetaEqui = new javax.swing.JTextField();
        txtDescricaoEqui = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        txtFuncionarioEqui = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        lblFotoPerfilEqui = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        txtCondEqui = new javax.swing.JTextArea();
        btnInserirImagemEqui = new javax.swing.JButton();
        btnAlterarImagemEqui = new javax.swing.JButton();
        btnExcluirImagemEqui = new javax.swing.JButton();

        jTextField1.setText("jTextField1");

        jLabel17.setText("jLabel17");

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setTitle("203 - Cadastrar Equipamento");
        setToolTipText("");

        jPanel6.setBackground(new java.awt.Color(255, 255, 255));
        jPanel6.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        tblEquipamentos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Etiqueta", "Filial", "Tipo", "Descrição", "Setor", "Funcionário", "Valor", "Quantidade", "Fornecedor", "Data da Entrada", "Data da Saída", "Status", "Marca", "Condições"
            }
        ));
        tblEquipamentos.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblEquipamentosMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblEquipamentos);

        jPanel6.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 370, 940, 310));

        jPanel11.setBackground(new java.awt.Color(255, 255, 255));
        jPanel11.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jPanel8.setBackground(new java.awt.Color(255, 0, 0));
        jPanel8.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblDevolvidos.setBackground(new java.awt.Color(255, 255, 255));
        lblDevolvidos.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        lblDevolvidos.setForeground(new java.awt.Color(255, 255, 255));
        lblDevolvidos.setText(":");
        jPanel8.add(lblDevolvidos, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 62, 150, -1));

        jPanel5.setBackground(new java.awt.Color(255, 51, 51));

        jLabel14.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel14.setText("Devolvidos");

        jLabel27.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/megacenter/icones/troca.png"))); // NOI18N

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel27, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel14, javax.swing.GroupLayout.DEFAULT_SIZE, 144, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel27, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel14, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        jPanel8.add(jPanel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 220, -1));

        jLabel24.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel24.setForeground(new java.awt.Color(255, 255, 255));
        jLabel24.setText("Equipamentos devolvidos");
        jPanel8.add(jLabel24, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 109, 200, -1));

        jPanel3.setBackground(new java.awt.Color(9, 132, 227));
        jPanel3.setPreferredSize(new java.awt.Dimension(134, 125));
        jPanel3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblValorTotal.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        lblValorTotal.setForeground(new java.awt.Color(255, 255, 255));
        lblValorTotal.setText(":");
        jPanel3.add(lblValorTotal, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 61, 180, -1));

        jPanel12.setBackground(new java.awt.Color(116, 185, 255));

        ValorTotalEquipamentos.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        ValorTotalEquipamentos.setText("Custo");

        jLabel26.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/megacenter/icones/despesas.png"))); // NOI18N

        javax.swing.GroupLayout jPanel12Layout = new javax.swing.GroupLayout(jPanel12);
        jPanel12.setLayout(jPanel12Layout);
        jPanel12Layout.setHorizontalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel12Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel26)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(ValorTotalEquipamentos, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(34, Short.MAX_VALUE))
        );
        jPanel12Layout.setVerticalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel26, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(ValorTotalEquipamentos, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(0, 12, Short.MAX_VALUE))
        );

        jPanel3.add(jPanel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 220, -1));

        jLabel28.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel28.setForeground(new java.awt.Color(255, 255, 255));
        jLabel28.setText("Despesas com equipamentos");
        jPanel3.add(jLabel28, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 108, 200, -1));

        jPanel2.setBackground(new java.awt.Color(0, 184, 148));
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblTotalEquipamentos.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        lblTotalEquipamentos.setForeground(new java.awt.Color(255, 255, 255));
        lblTotalEquipamentos.setText(":");
        jPanel2.add(lblTotalEquipamentos, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 60, 170, -1));

        jLabel23.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel23.setForeground(new java.awt.Color(255, 255, 255));
        jLabel23.setText("Equipamentos cadastrado");
        jPanel2.add(jLabel23, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 107, 180, -1));

        jPanel7.setBackground(new java.awt.Color(0, 204, 102));

        jLabel12.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel12.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/megacenter/icones/Icon_Eletronico.png"))); // NOI18N
        jLabel12.setText("Todos");

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(187, Short.MAX_VALUE))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel12)
                .addContainerGap(15, Short.MAX_VALUE))
        );

        jPanel2.add(jPanel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 300, 60));

        jLabel13.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabel13.setText("Painel Dashboard");

        jPanel13.setBackground(new java.awt.Color(255, 255, 0));

        jPanel14.setBackground(new java.awt.Color(255, 255, 102));

        jLabel15.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel15.setText("Pendente");

        javax.swing.GroupLayout jPanel14Layout = new javax.swing.GroupLayout(jPanel14);
        jPanel14.setLayout(jPanel14Layout);
        jPanel14Layout.setHorizontalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel14Layout.setVerticalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel15)
                .addContainerGap(24, Short.MAX_VALUE))
        );

        lblPendentes.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        lblPendentes.setText(":");

        jLabel25.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel25.setText("Equipamentos pendentes");

        javax.swing.GroupLayout jPanel13Layout = new javax.swing.GroupLayout(jPanel13);
        jPanel13.setLayout(jPanel13Layout);
        jPanel13Layout.setHorizontalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel14, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel25, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblPendentes, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel13Layout.setVerticalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addComponent(jPanel14, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 8, Short.MAX_VALUE)
                .addComponent(lblPendentes)
                .addGap(18, 18, 18)
                .addComponent(jLabel25)
                .addContainerGap())
        );

        jPanel15.setBackground(new java.awt.Color(0, 204, 0));

        lblAtivos.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        lblAtivos.setText(":");

        jPanel16.setBackground(new java.awt.Color(0, 204, 102));

        jLabel35.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel35.setText("Ativos");

        javax.swing.GroupLayout jPanel16Layout = new javax.swing.GroupLayout(jPanel16);
        jPanel16.setLayout(jPanel16Layout);
        jPanel16Layout.setHorizontalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel16Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel35, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel16Layout.setVerticalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel16Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel35)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel36.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel36.setText("Equipamentos instalados");

        javax.swing.GroupLayout jPanel15Layout = new javax.swing.GroupLayout(jPanel15);
        jPanel15.setLayout(jPanel15Layout);
        jPanel15Layout.setHorizontalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel16, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel15Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblAtivos, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel36))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel15Layout.setVerticalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel15Layout.createSequentialGroup()
                .addComponent(jPanel16, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblAtivos)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel36)
                .addGap(12, 12, 12))
        );

        jPanel17.setBackground(new java.awt.Color(255, 204, 0));

        lblReservas.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        lblReservas.setText(":");

        jLabel33.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel33.setText("Reserva");

        jLabel34.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel34.setText("Equipamentos reservas");

        javax.swing.GroupLayout jPanel17Layout = new javax.swing.GroupLayout(jPanel17);
        jPanel17.setLayout(jPanel17Layout);
        jPanel17Layout.setHorizontalGroup(
            jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel17Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblReservas, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel33, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel34))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel17Layout.setVerticalGroup(
            jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel17Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel33)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lblReservas)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel34)
                .addContainerGap())
        );

        txtQuantidadePorTipo.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        txtQuantidadePorTipo.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtQuantidadePorTipo.setEnabled(false);

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel13)
                    .addGroup(jPanel11Layout.createSequentialGroup()
                        .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 220, Short.MAX_VALUE)
                            .addComponent(jPanel15, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(12, 12, 12)
                        .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel17, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, 220, Short.MAX_VALUE)
                            .addComponent(jPanel13, javax.swing.GroupLayout.PREFERRED_SIZE, 220, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtQuantidadePorTipo, javax.swing.GroupLayout.PREFERRED_SIZE, 220, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel11Layout.createSequentialGroup()
                .addGap(7, 7, 7)
                .addComponent(jLabel13)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel11Layout.createSequentialGroup()
                        .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jPanel17, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel15, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel13, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addComponent(txtQuantidadePorTipo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel6.add(jPanel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 940, 350));

        jTabbedPane2.addTab("Dashboard", jPanel6);

        jPanel9.setBackground(new java.awt.Color(255, 255, 255));

        jPanel4.setBackground(new java.awt.Color(255, 255, 204));
        jPanel4.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));
        jPanel4.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        btnCadastrarEqui.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        btnCadastrarEqui.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/megacenter/icones/adicionar.png"))); // NOI18N
        btnCadastrarEqui.setText("Cadastrar");
        btnCadastrarEqui.setToolTipText("Cadastrar");
        btnCadastrarEqui.setBorder(null);
        btnCadastrarEqui.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCadastrarEquiActionPerformed(evt);
            }
        });
        jPanel4.add(btnCadastrarEqui, new org.netbeans.lib.awtextra.AbsoluteConstraints(830, 10, 100, 30));

        btnAtualizarEqui.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        btnAtualizarEqui.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/megacenter/icones/editar.png"))); // NOI18N
        btnAtualizarEqui.setText("  Editar");
        btnAtualizarEqui.setBorder(null);
        btnAtualizarEqui.setMaximumSize(new java.awt.Dimension(63, 17));
        btnAtualizarEqui.setMinimumSize(new java.awt.Dimension(63, 17));
        btnAtualizarEqui.setPreferredSize(new java.awt.Dimension(81, 23));
        btnAtualizarEqui.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAtualizarEquiActionPerformed(evt);
            }
        });
        jPanel4.add(btnAtualizarEqui, new org.netbeans.lib.awtextra.AbsoluteConstraints(720, 10, 100, 30));

        btnConsultarEqui.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        btnConsultarEqui.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/megacenter/icones/Icon_Pesquisa.png"))); // NOI18N
        btnConsultarEqui.setText("Consultar");
        btnConsultarEqui.setMaximumSize(new java.awt.Dimension(63, 17));
        btnConsultarEqui.setMinimumSize(new java.awt.Dimension(63, 17));
        btnConsultarEqui.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnConsultarEquiActionPerformed(evt);
            }
        });
        jPanel4.add(btnConsultarEqui, new org.netbeans.lib.awtextra.AbsoluteConstraints(610, 10, -1, 30));

        btnSalvarEqui.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        btnSalvarEqui.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/megacenter/icones/Icon_salvar.png"))); // NOI18N
        btnSalvarEqui.setText("Salvar");
        btnSalvarEqui.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSalvarEquiActionPerformed(evt);
            }
        });
        jPanel4.add(btnSalvarEqui, new org.netbeans.lib.awtextra.AbsoluteConstraints(500, 10, 100, 30));

        btnExcluirEqui.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        btnExcluirEqui.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/megacenter/icones/excluir.png"))); // NOI18N
        btnExcluirEqui.setText("  Excluir");
        btnExcluirEqui.setBorder(null);
        btnExcluirEqui.setPreferredSize(new java.awt.Dimension(81, 23));
        btnExcluirEqui.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExcluirEquiActionPerformed(evt);
            }
        });
        jPanel4.add(btnExcluirEqui, new org.netbeans.lib.awtextra.AbsoluteConstraints(400, 10, 100, 30));

        btnCancelar.setText("Cancelar");
        btnCancelar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelarActionPerformed(evt);
            }
        });
        jPanel4.add(btnCancelar, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 10, -1, 30));

        txtDiretorioImagemEqui.setEditable(false);
        txtDiretorioImagemEqui.setBackground(new java.awt.Color(255, 255, 255));
        txtDiretorioImagemEqui.setFont(new java.awt.Font("Tahoma", 0, 3)); // NOI18N
        txtDiretorioImagemEqui.setForeground(new java.awt.Color(255, 255, 255));
        txtDiretorioImagemEqui.setBorder(null);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));

        jLabel19.setText("Equipamento");

        txtPesquisaEqui.setBackground(new java.awt.Color(255, 255, 204));
        txtPesquisaEqui.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtPesquisaEquiActionPerformed(evt);
            }
        });
        txtPesquisaEqui.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtPesquisaEquiKeyReleased(evt);
            }
        });

        jLabel18.setText("Filial");

        cboFiltroFilialEqui.setBackground(new java.awt.Color(255, 255, 204));
        cboFiltroFilialEqui.setModel(new javax.swing.DefaultComboBoxModel(new String[] { " " }));

        txtFiltroDescricaoEqui.setBackground(new java.awt.Color(255, 255, 204));
        txtFiltroDescricaoEqui.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtFiltroDescricaoEquiKeyReleased(evt);
            }
        });

        cboFiltroTipoEqui.setBackground(new java.awt.Color(255, 255, 204));
        cboFiltroTipoEqui.setModel(new javax.swing.DefaultComboBoxModel(new String[] { " " }));

        jLabel16.setText("Descrição");

        jLabel21.setText("Tipo");

        cboFiltroUsuarioEqui.setBackground(new java.awt.Color(255, 255, 204));
        cboFiltroUsuarioEqui.setModel(new javax.swing.DefaultComboBoxModel(new String[] { " ", " " }));

        jLabel22.setText("Usuário");

        tblEquipamentos3.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Filial", "Nº Etiqueta", "Tipo", "Descrição", "Setor", "Funcionário", "Valor", "Quantidade", "Fornecedor", "Data Entrada", "Data Saída", "Status", "Marca", "Condição"
            }
        ));
        tblEquipamentos3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblEquipamentos3MouseClicked(evt);
            }
        });
        jScrollPane4.setViewportView(tblEquipamentos3);

        bntBuscarEqui.setBackground(new java.awt.Color(255, 255, 204));
        bntBuscarEqui.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        bntBuscarEqui.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/megacenter/icones/Icon_Pesquisa.png"))); // NOI18N
        bntBuscarEqui.setText("Buscar");
        bntBuscarEqui.setPreferredSize(new java.awt.Dimension(69, 25));
        bntBuscarEqui.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bntBuscarEquiActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane4)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(txtPesquisaEqui, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(7, 7, 7)
                                .addComponent(cboFiltroFilialEqui, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(7, 7, 7)
                                .addComponent(txtFiltroDescricaoEqui, javax.swing.GroupLayout.PREFERRED_SIZE, 223, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(7, 7, 7)
                                .addComponent(cboFiltroTipoEqui, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(7, 7, 7)
                                .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(7, 7, 7)
                                .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(145, 145, 145)
                                .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel22, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cboFiltroUsuarioEqui, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(bntBuscarEqui, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(13, 13, 13)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel19)
                    .addComponent(jLabel18)
                    .addComponent(jLabel16)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel21)
                        .addComponent(jLabel22)))
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(7, 7, 7)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtPesquisaEqui, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cboFiltroFilialEqui, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtFiltroDescricaoEqui, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(cboFiltroTipoEqui, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(cboFiltroUsuarioEqui, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(7, 7, 7))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(bntBuscarEqui, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 206, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel10.setBackground(new java.awt.Color(255, 255, 255));
        jPanel10.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));
        jPanel10.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel10.add(cboSetorEqui, new org.netbeans.lib.awtextra.AbsoluteConstraints(278, 95, 135, -1));

        cboTipoEqui.setModel(new javax.swing.DefaultComboBoxModel(new String[] { " ", "Adaptador", "Desktop", "Monitor", "Leitor", "Mouse", "Teclado" }));
        jPanel10.add(cboTipoEqui, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 95, 146, -1));

        cboStatusEqui.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Ativo", "Reserva", "Devolvido" }));
        jPanel10.add(cboStatusEqui, new org.netbeans.lib.awtextra.AbsoluteConstraints(278, 147, 135, -1));

        jPanel10.add(cboFornecedorEqui, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 147, 146, -1));

        jLabel32.setText("Fornecedor");
        jPanel10.add(jLabel32, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 124, 90, -1));

        jLabel30.setText("Status");
        jPanel10.add(jLabel30, new org.netbeans.lib.awtextra.AbsoluteConstraints(278, 124, 68, -1));

        jLabel2.setText("Tipo *");
        jPanel10.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 72, 50, -1));

        jLabel5.setText("Setor *");
        jPanel10.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(278, 72, 50, -1));
        jPanel10.add(dtEntradaEqui, new org.netbeans.lib.awtextra.AbsoluteConstraints(13, 95, 120, -1));

        jLabel11.setText("Dt. de Entrada *");
        jPanel10.add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(13, 69, 110, -1));

        jLabel9.setText("Dt. de Saída");
        jPanel10.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(139, 69, 90, -1));
        jPanel10.add(dtSaidaEqui, new org.netbeans.lib.awtextra.AbsoluteConstraints(139, 95, 132, -1));
        jPanel10.add(txtMarcaEqui, new org.netbeans.lib.awtextra.AbsoluteConstraints(573, 95, 150, -1));

        jLabel3.setText("Marca");
        jPanel10.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(573, 72, 60, -1));

        jLabel29.setText("Quantidade:");
        jPanel10.add(jLabel29, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 124, -1, -1));
        jPanel10.add(txtQuantidadeEqui, new org.netbeans.lib.awtextra.AbsoluteConstraints(13, 147, 120, -1));

        txtValorEqui.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED, null, new java.awt.Color(204, 204, 204), null, null));
        jPanel10.add(txtValorEqui, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 147, 131, -1));

        jLabel4.setText("Valor:");
        jPanel10.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(13, 124, 43, -1));

        txtIdEqui.setEditable(false);
        txtIdEqui.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED, null, new java.awt.Color(204, 204, 204), null, null));
        txtIdEqui.setEnabled(false);
        txtIdEqui.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtIdEquiActionPerformed(evt);
            }
        });
        jPanel10.add(txtIdEqui, new org.netbeans.lib.awtextra.AbsoluteConstraints(13, 37, 46, -1));

        jLabel8.setText("Código");
        jPanel10.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(13, 14, 52, -1));

        jLabel31.setText("Filial");
        jPanel10.add(jLabel31, new org.netbeans.lib.awtextra.AbsoluteConstraints(75, 14, 54, -1));

        cboFilialEqui.setModel(new javax.swing.DefaultComboBoxModel(new String[] { " " }));
        jPanel10.add(cboFilialEqui, new org.netbeans.lib.awtextra.AbsoluteConstraints(71, 37, 60, -1));

        txtEtiquetaEqui.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED, null, new java.awt.Color(204, 204, 204), null, null));
        jPanel10.add(txtEtiquetaEqui, new org.netbeans.lib.awtextra.AbsoluteConstraints(143, 37, 128, -1));

        txtDescricaoEqui.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED, null, new java.awt.Color(204, 204, 204), null, null));
        jPanel10.add(txtDescricaoEqui, new org.netbeans.lib.awtextra.AbsoluteConstraints(278, 37, 288, -1));

        jLabel7.setText("Descrição");
        jPanel10.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(278, 14, -1, -1));

        jLabel1.setText("Nº. Etiqueta *");
        jPanel10.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(139, 14, -1, -1));

        txtFuncionarioEqui.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED, null, new java.awt.Color(204, 204, 204), null, null));
        jPanel10.add(txtFuncionarioEqui, new org.netbeans.lib.awtextra.AbsoluteConstraints(573, 37, 150, -1));

        jLabel6.setText("Funcionário *");
        jPanel10.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(573, 14, 80, -1));

        lblFotoPerfilEqui.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED, null, new java.awt.Color(204, 204, 204), null, null));
        jPanel10.add(lblFotoPerfilEqui, new org.netbeans.lib.awtextra.AbsoluteConstraints(741, 37, 190, 132));

        jLabel20.setText("Imagem");
        jPanel10.add(jLabel20, new org.netbeans.lib.awtextra.AbsoluteConstraints(741, 14, 60, -1));

        jLabel10.setText("Condições do Equipamento");
        jPanel10.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(13, 176, 168, -1));

        txtCondEqui.setColumns(20);
        txtCondEqui.setRows(5);
        txtCondEqui.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED, null, new java.awt.Color(204, 204, 204), null, null));
        jScrollPane2.setViewportView(txtCondEqui);

        jPanel10.add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(13, 201, 553, -1));

        btnInserirImagemEqui.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        btnInserirImagemEqui.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/megacenter/icones/imagem.png"))); // NOI18N
        btnInserirImagemEqui.setText("Adicionar");
        btnInserirImagemEqui.setBorder(null);
        btnInserirImagemEqui.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnInserirImagemEquiActionPerformed(evt);
            }
        });
        jPanel10.add(btnInserirImagemEqui, new org.netbeans.lib.awtextra.AbsoluteConstraints(830, 190, 99, 30));

        btnAlterarImagemEqui.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        btnAlterarImagemEqui.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/megacenter/icones/imagem.png"))); // NOI18N
        btnAlterarImagemEqui.setText("Alterar");
        btnAlterarImagemEqui.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAlterarImagemEquiActionPerformed(evt);
            }
        });
        jPanel10.add(btnAlterarImagemEqui, new org.netbeans.lib.awtextra.AbsoluteConstraints(830, 230, 99, 28));

        btnExcluirImagemEqui.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        btnExcluirImagemEqui.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/megacenter/icones/imagem.png"))); // NOI18N
        btnExcluirImagemEqui.setText("Excluir");
        btnExcluirImagemEqui.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExcluirImagemEquiActionPerformed(evt);
            }
        });
        jPanel10.add(btnExcluirImagemEqui, new org.netbeans.lib.awtextra.AbsoluteConstraints(830, 270, 99, 28));

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, 942, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtDiretorioImagemEqui, javax.swing.GroupLayout.PREFERRED_SIZE, 167, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(17, 17, 17))
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel9Layout.createSequentialGroup()
                        .addGap(168, 168, 168)
                        .addComponent(txtDiretorioImagemEqui, javax.swing.GroupLayout.PREFERRED_SIZE, 5, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel9Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, 313, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jTabbedPane2.addTab("Cadastro", jPanel9);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(4, 4, 4)
                .addComponent(jTabbedPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 970, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jTabbedPane2)
                .addContainerGap())
        );

        getAccessibleContext().setAccessibleName("203 - Cadastro de Equipamento");

        setBounds(0, 0, 1002, 770);
    }// </editor-fold>//GEN-END:initComponents

    private void btnInserirImagemEquiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnInserirImagemEquiActionPerformed
        // Criando o metodo para adicionar imagem
        JFileChooser arquivoimagem = new JFileChooser();
        arquivoimagem.setDialogTitle("Selecione uma Imagem");
        arquivoimagem.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int op = arquivoimagem.showOpenDialog(this);
        if (op == JFileChooser.APPROVE_OPTION) {
            File file = new File("");
            file = arquivoimagem.getSelectedFile();
            String fileCodigo = file.getAbsolutePath();
            txtDiretorioImagemEqui.setText(fileCodigo);
            ImageIcon imagem = new ImageIcon(file.getPath());
            lblFotoPerfilEqui.setIcon(new ImageIcon(imagem.getImage().getScaledInstance(lblFotoPerfilEqui.getWidth(), lblFotoPerfilEqui.getHeight(), Image.SCALE_DEFAULT)));
        }
    }//GEN-LAST:event_btnInserirImagemEquiActionPerformed

    private void btnCadastrarEquiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCadastrarEquiActionPerformed
        // Chamando o metodo cadastrar
        cadastrar_equipamento();
    }//GEN-LAST:event_btnCadastrarEquiActionPerformed

    private void btnAtualizarEquiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAtualizarEquiActionPerformed
        atualizarTotalEquipamentos();
    }//GEN-LAST:event_btnAtualizarEquiActionPerformed

    private void btnExcluirEquiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExcluirEquiActionPerformed
        // chamando o método excluir
        excluir_equipamento();
    }//GEN-LAST:event_btnExcluirEquiActionPerformed

    private void txtIdEquiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtIdEquiActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtIdEquiActionPerformed

    private void tblEquipamentosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblEquipamentosMouseClicked
        // chamando o metodo preencher campos
        preencher_campos();
    }//GEN-LAST:event_tblEquipamentosMouseClicked

    private void btnAlterarImagemEquiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAlterarImagemEquiActionPerformed
        // TODO add your handling code here:
        alterarImagemEquipamento();
    }//GEN-LAST:event_btnAlterarImagemEquiActionPerformed

    private void btnExcluirImagemEquiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExcluirImagemEquiActionPerformed
        // TODO add your handling code here:
        excluirImagemEquipamento();
    }//GEN-LAST:event_btnExcluirImagemEquiActionPerformed

    private void btnSalvarEquiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSalvarEquiActionPerformed
        // TODO add your handling code here:
        salvarEquipamento();
    }//GEN-LAST:event_btnSalvarEquiActionPerformed

    private void bntBuscarEquiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bntBuscarEquiActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_bntBuscarEquiActionPerformed

    private void txtFiltroDescricaoEquiKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtFiltroDescricaoEquiKeyReleased
        // TODO add your handling code here:
        filtro_pesquisar_equipamento();
    }//GEN-LAST:event_txtFiltroDescricaoEquiKeyReleased

    private void txtPesquisaEquiKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtPesquisaEquiKeyReleased
        // chamando o metodo pesquisar equiámentos
        exibir_pesquisar_equipamento();
    }//GEN-LAST:event_txtPesquisaEquiKeyReleased

    private void txtPesquisaEquiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtPesquisaEquiActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtPesquisaEquiActionPerformed

    private void tblEquipamentos3MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblEquipamentos3MouseClicked
        // chamando o metodo preencher campos
        exibir_campos();
    }//GEN-LAST:event_tblEquipamentos3MouseClicked

    private void btnConsultarEquiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnConsultarEquiActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnConsultarEquiActionPerformed

    private void btnCancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelarActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnCancelarActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel ValorTotalEquipamentos;
    private javax.swing.JButton bntBuscarEqui;
    private javax.swing.JButton btnAlterarImagemEqui;
    private javax.swing.JButton btnAtualizarEqui;
    private javax.swing.JButton btnCadastrarEqui;
    private javax.swing.JButton btnCancelar;
    private javax.swing.JButton btnConsultarEqui;
    private javax.swing.JButton btnExcluirEqui;
    private javax.swing.JButton btnExcluirImagemEqui;
    private javax.swing.JButton btnInserirImagemEqui;
    private javax.swing.JButton btnSalvarEqui;
    private javax.swing.JComboBox cboFilialEqui;
    private javax.swing.JComboBox cboFiltroFilialEqui;
    private javax.swing.JComboBox cboFiltroTipoEqui;
    private javax.swing.JComboBox cboFiltroUsuarioEqui;
    private javax.swing.JComboBox cboFornecedorEqui;
    private javax.swing.JComboBox<String> cboSetorEqui;
    private javax.swing.JComboBox cboStatusEqui;
    private javax.swing.JComboBox<String> cboTipoEqui;
    private com.toedter.calendar.JDateChooser dtEntradaEqui;
    private com.toedter.calendar.JDateChooser dtSaidaEqui;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel15;
    private javax.swing.JPanel jPanel16;
    private javax.swing.JPanel jPanel17;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JTabbedPane jTabbedPane2;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JLabel lblAtivos;
    private javax.swing.JLabel lblDevolvidos;
    private javax.swing.JLabel lblFotoPerfilEqui;
    private javax.swing.JLabel lblPendentes;
    private javax.swing.JLabel lblReservas;
    private javax.swing.JLabel lblTotalEquipamentos;
    private javax.swing.JLabel lblValorTotal;
    private javax.swing.JTable tblEquipamentos;
    private javax.swing.JTable tblEquipamentos3;
    private javax.swing.JTextArea txtCondEqui;
    private javax.swing.JTextField txtDescricaoEqui;
    private javax.swing.JTextField txtDiretorioImagemEqui;
    private javax.swing.JTextField txtEtiquetaEqui;
    private javax.swing.JTextField txtFiltroDescricaoEqui;
    private javax.swing.JTextField txtFuncionarioEqui;
    private javax.swing.JTextField txtIdEqui;
    private javax.swing.JTextField txtMarcaEqui;
    private javax.swing.JTextField txtPesquisaEqui;
    private javax.swing.JTextField txtQuantidadeEqui;
    private javax.swing.JTextField txtQuantidadePorTipo;
    private javax.swing.JTextField txtValorEqui;
    // End of variables declaration//GEN-END:variables
}
