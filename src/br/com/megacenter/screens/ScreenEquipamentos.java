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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import net.proteanit.sql.DbUtils;

// Imports do JFreeChart
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.chart.plot.PlotOrientation;


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
        bloquearEdicaoTabela();
        
    }

    // Método auxiliar para evitar NullPointerException
    private String valorOuVazio(Object valor) {
        return (valor == null) ? "" : valor.toString();
    }

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
    }

    // criando o metodo para pesquisar os equipamentos pelo número da etiqueta
    private void pesquisar_equipamento() {
        String sql = "Select idequipamento as Código, etiqueta_equipamento as Etiqueta, data_cadastrado as Data, tipo as Tipo, marca as Marca, setor as Setor"
                + ", funcionario as Funcionário, valor as Valor, foto_equipamento as Imagem, descricao as Descrição, condicoes_equipamento as Condições, status as Status from equipamentos where etiqueta_equipamento "
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

    // criando o metodo para pesquisar os equipamentos pelo número da etiqueta
    private void exibir_pesquisar_equipamento() {
        String sql = "Select idequipamento as Código, etiqueta_equipamento as Etiqueta, data_cadastrado as Data, tipo as Tipo, marca as Marca, setor as Setor"
                + ", funcionario as Funcionário, valor as Valor, foto_equipamento as Imagem, descricao as Descrição, condicoes_equipamento as Condições, status as Status from equipamentos where etiqueta_equipamento "
                + "like ?";
        try {
            pst = conexao.prepareStatement(sql);
            pst.setString(1, txtPesquisaEqui.getText() + "%");
            rs = pst.executeQuery();
            tblEquipamentos3.setModel(DbUtils.resultSetToTableModel(rs));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }

    // Método para exibir todos os equipamentos cadastrados
    private void exibir_todos_equipamentos_dashboard() {
        String sql = "SELECT idequipamento as Código, etiqueta_equipamento as Etiqueta, data_cadastrado as Data, tipo as Tipo, "
                + "marca as Marca, setor as Setor, funcionario as Funcionário, valor as Valor, foto_equipamento as Imagem, "
                + "descricao as Descrição, condicoes_equipamento as Condições, status as Status FROM equipamentos";
        try {
            pst = conexao.prepareStatement(sql);
            rs = pst.executeQuery();
            tblEquipamentos.setModel(DbUtils.resultSetToTableModel(rs));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }
  

    // Método para deixar a tabela somente para visualização
    private void bloquearEdicaoTabela() {
        // Impede edição de células
        tblEquipamentos.setDefaultEditor(Object.class, null);

        // Impede que o usuário arraste e mude a posição das colunas
        tblEquipamentos.getTableHeader().setReorderingAllowed(false);

        // Impede o redimensionamento das colunas (opcional, se quiser também travar)
        tblEquipamentos.getTableHeader().setResizingAllowed(false);
    }

    private void preencher_campos() {
        try {
            conexao = ModuloConexao.conector(); // Garante que está conectado ao banco

            int preencher = tblEquipamentos.getSelectedRow();

            // Preenchendo os campos de texto com tratamento para valores null
            txtIdEqui.setText(valorOuVazio(tblEquipamentos.getModel().getValueAt(preencher, 0)));
            txtEtiquetaEqui.setText(valorOuVazio(tblEquipamentos.getModel().getValueAt(preencher, 1)));
            // DATA: trate corretamente conforme o tipo vindo do model
            Object valorData = tblEquipamentos.getModel().getValueAt(preencher, 2);
            if (valorData instanceof java.util.Date) {
                // Timestamp também é instanceOf java.util.Date
                txtDataEquiCadastrado.setDate((java.util.Date) valorData);
            } else if (valorData instanceof String) {
                String s = ((String) valorData).trim();
                if (!s.isEmpty()) {
                    try {
                        // ajustar o padrão conforme o formato da String (ex: "yyyy-MM-dd" ou "dd/MM/yyyy")
                        java.util.Date dataConvertida = new java.text.SimpleDateFormat("yyyy-MM-dd").parse(s);
                        txtDataEquiCadastrado.setDate(dataConvertida);
                    } catch (java.text.ParseException ex) {
                        txtDataEquiCadastrado.setDate(null); // ou tratar de outra forma
                    }
                } else {
                    txtDataEquiCadastrado.setDate(null);
                }
            } else {
                txtDataEquiCadastrado.setDate(null);
            }
            cboTipoEqui.setSelectedItem(valorOuVazio(tblEquipamentos.getModel().getValueAt(preencher, 3)));
            txtMarcaEqui.setText(valorOuVazio(tblEquipamentos.getModel().getValueAt(preencher, 4)));
            cboSetorEqui.setSelectedItem(valorOuVazio(tblEquipamentos.getModel().getValueAt(preencher, 5)));
            txtFuncionarioEqui.setText(valorOuVazio(tblEquipamentos.getModel().getValueAt(preencher, 6)));
            txtValorEqui.setText(valorOuVazio(tblEquipamentos.getModel().getValueAt(preencher, 7)));
            txtDescricaoEqui.setText(valorOuVazio(tblEquipamentos.getModel().getValueAt(preencher, 9)));
            txtCondEqui.setText(valorOuVazio(tblEquipamentos.getModel().getValueAt(preencher, 10)));
            cboStatusEqui.setSelectedItem(valorOuVazio(tblEquipamentos.getModel().getValueAt(preencher, 11)));

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

    // Criando o metodo para exibir as informações nos campos da tabela
    private void exibir_campos() {
        try {
            conexao = ModuloConexao.conector(); // Garante que está conectado ao banco

            int preencher = tblEquipamentos3.getSelectedRow();

            // Preenchendo os campos de texto com tratamento para valores null
            txtIdEqui.setText(valorOuVazio(tblEquipamentos3.getModel().getValueAt(preencher, 0)));
            txtEtiquetaEqui.setText(valorOuVazio(tblEquipamentos3.getModel().getValueAt(preencher, 1)));
            // DATA: trate corretamente conforme o tipo vindo do model
            // ----------------------
            Object valorData = tblEquipamentos.getModel().getValueAt(preencher, 2);
            if (valorData instanceof java.util.Date) {
                // Timestamp também é instanceOf java.util.Date
                txtDataEquiCadastrado.setDate((java.util.Date) valorData);
            } else if (valorData instanceof String) {
                String s = ((String) valorData).trim();
                if (!s.isEmpty()) {
                    try {
                        // ajustar o padrão conforme o formato da String (ex: "yyyy-MM-dd" ou "dd/MM/yyyy")
                        java.util.Date dataConvertida = new java.text.SimpleDateFormat("yyyy-MM-dd").parse(s);
                        txtDataEquiCadastrado.setDate(dataConvertida);
                    } catch (java.text.ParseException ex) {
                        txtDataEquiCadastrado.setDate(null); // ou tratar de outra forma
                    }
                } else {
                    txtDataEquiCadastrado.setDate(null);
                }
            } else {
                txtDataEquiCadastrado.setDate(null);
            }
            cboTipoEqui.setSelectedItem(valorOuVazio(tblEquipamentos3.getModel().getValueAt(preencher, 3)));
            txtMarcaEqui.setText(valorOuVazio(tblEquipamentos3.getModel().getValueAt(preencher, 4)));
            cboSetorEqui.setSelectedItem(valorOuVazio(tblEquipamentos3.getModel().getValueAt(preencher, 5)));
            txtFuncionarioEqui.setText(valorOuVazio(tblEquipamentos3.getModel().getValueAt(preencher, 6)));
            txtValorEqui.setText(valorOuVazio(tblEquipamentos3.getModel().getValueAt(preencher, 7)));
            txtDescricaoEqui.setText(valorOuVazio(tblEquipamentos3.getModel().getValueAt(preencher, 9)));
            txtCondEqui.setText(valorOuVazio(tblEquipamentos.getModel().getValueAt(preencher, 10)));
            cboStatusEqui.setSelectedItem(valorOuVazio(tblEquipamentos.getModel().getValueAt(preencher, 11)));
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
        String sql = "INSERT INTO equipamentos ("
                + "etiqueta_equipamento, tipo, Data_cadastrado, marca, setor, funcionario, valor, foto_equipamento, descricao, condicoes_equipamento, status"
                + ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?,?,?)";

        // Validação de campos obrigatórios
        if (txtEtiquetaEqui.getText().trim().isEmpty()
                || txtFuncionarioEqui.getText().trim().isEmpty()
                || cboSetorEqui.getSelectedItem() == null
                || cboTipoEqui.getSelectedItem() == null
                || cboSetorEqui.getSelectedItem().toString().trim().isEmpty()
                || cboTipoEqui.getSelectedItem().toString().trim().isEmpty()
                || txtDataEquiCadastrado.getDate() == null) {

            JOptionPane.showMessageDialog(null, "Por favor, preencha todos os campos obrigatórios.");
            return;
        }

        // Validação da imagem
        File imagem = new File(txtDiretorioImagemEqui.getText());
        if (!imagem.exists() || !imagem.isFile()) {
            JOptionPane.showMessageDialog(null, "Imagem não encontrada ou caminho inválido.");
            return;
        }

        try (PreparedStatement pst = conexao.prepareStatement(sql);
                FileInputStream fis = new FileInputStream(imagem)) {

            pst.setString(1, txtEtiquetaEqui.getText().trim());
            pst.setString(2, cboTipoEqui.getSelectedItem().toString());
            java.util.Date utilDate = txtDataEquiCadastrado.getDate();
            java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());
            pst.setDate(3, sqlDate);
            pst.setString(4, txtMarcaEqui.getText().trim());
            pst.setString(5, cboSetorEqui.getSelectedItem().toString());
            pst.setString(6, txtFuncionarioEqui.getText().trim());
            // 7 - Valor (conversão para número)
            try {
                double valor = Double.parseDouble(txtValorEqui.getText().replace(",", "."));
                pst.setDouble(7, valor);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Valor inválido. Digite um número válido.");
                return;
            }
            pst.setBinaryStream(8, fis, (int) imagem.length());
            pst.setString(9, txtDescricaoEqui.getText().trim());
            pst.setString(10, txtCondEqui.getText().trim());
            pst.setString(11, cboStatusEqui.getSelectedItem().toString());

            // Executa inserção
            int adicionado = pst.executeUpdate();
            if (adicionado > 0) {
                JOptionPane.showMessageDialog(null, "Equipamento cadastrado com sucesso.");
                limpar_campos(); // limpando os dados após o cadastro
                exibir_todos_equipamentos_dashboard();
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao salvar as informações no banco: " + e.getMessage());
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Erro ao carregar a imagem: " + e.getMessage());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Erro inesperado: " + e.getMessage());
        }
    }

    // Criando o método para alterar um equipamento
    private void alterar_equipamento() {
        String sql = "UPDATE equipamentos SET etiqueta_equipamento=?, tipo=?, marca=?, setor=?, funcionario=?, valor=?, foto_equipamento=?, "
                + "descricao=?, condicoes_equipamento=?, status=? WHERE idequipamento=?";

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
            pst.setInt(10, Integer.parseInt(txtIdEqui.getText())); // idequipamento (numérico)
            pst.setString(11, cboStatusEqui.getSelectedItem().toString());

            int atualizado = pst.executeUpdate();
            if (atualizado > 0) {
                JOptionPane.showMessageDialog(null, "Equipamento atualizado com sucesso!");
                limpar_campos(); // Limpando os campos
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
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, e);
            }
        }
    }

    // limpar campos e habilitar os botões e gerenciar os botões
    private void limpar_campos() {
        // limpando os campos
        txtIdEqui.setText(null);
        txtEtiquetaEqui.setText(null);
        txtDataEquiCadastrado.setDate(null);
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
        jPanel2 = new javax.swing.JPanel();
        lblTotalEquipamentos = new javax.swing.JLabel();
        jPanel11 = new javax.swing.JPanel();
        jLabel12 = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        lblValorTotal = new javax.swing.JLabel();
        jPanel12 = new javax.swing.JPanel();
        ValorTotalEquipamentos = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        jPanel8 = new javax.swing.JPanel();
        jLabel15 = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        jLabel14 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        txtQuantidadePorTipo = new javax.swing.JTextField();
        jPanel1 = new javax.swing.JPanel();
        jPanel7 = new javax.swing.JPanel();
        jLabel16 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblEquipamentos = new javax.swing.JTable();
        jPanel9 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        txtCondEqui = new javax.swing.JTextArea();
        lblFotoPerfilEqui = new javax.swing.JLabel();
        cboTipoEqui = new javax.swing.JComboBox<String>();
        txtValorEqui = new javax.swing.JTextField();
        txtFuncionarioEqui = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        cboSetorEqui = new javax.swing.JComboBox<String>();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        txtEtiquetaEqui = new javax.swing.JTextField();
        txtMarcaEqui = new javax.swing.JTextField();
        cboStatusEqui = new javax.swing.JComboBox();
        jLabel10 = new javax.swing.JLabel();
        jDateChooser1 = new com.toedter.calendar.JDateChooser();
        txtDataEquiCadastrado = new com.toedter.calendar.JDateChooser();
        jTextField2 = new javax.swing.JTextField();
        txtDescricaoEqui = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        txtIdEqui = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel30 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        txtDiretorioImagemEqui = new javax.swing.JTextField();
        jTextField3 = new javax.swing.JTextField();
        jLabel31 = new javax.swing.JLabel();
        jTextField4 = new javax.swing.JTextField();
        jLabel32 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        btnExcluirEqui = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        btnAtualizarEqui = new javax.swing.JButton();
        btnCadastrarEqui = new javax.swing.JButton();
        btnInserirImagemEqui = new javax.swing.JButton();
        jLabel19 = new javax.swing.JLabel();
        txtPesquisaEqui = new javax.swing.JTextField();
        jScrollPane4 = new javax.swing.JScrollPane();
        tblEquipamentos3 = new javax.swing.JTable();
        jLabel20 = new javax.swing.JLabel();
        jLabel33 = new javax.swing.JLabel();

        jTextField1.setText("jTextField1");

        jLabel17.setText("jLabel17");

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setTitle("203 - Cadastrar Equipamento");
        setToolTipText("");
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel6.setBackground(new java.awt.Color(102, 102, 102));

        jPanel2.setBackground(new java.awt.Color(0, 184, 148));

        lblTotalEquipamentos.setFont(new java.awt.Font("Tahoma", 1, 30)); // NOI18N
        lblTotalEquipamentos.setForeground(new java.awt.Color(255, 255, 255));
        lblTotalEquipamentos.setText(":");

        jPanel11.setBackground(new java.awt.Color(85, 239, 196));

        jLabel12.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel12.setText("Ativos");

        jLabel28.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/megacenter/icones/Eletronico.png"))); // NOI18N

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel11Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel28)
                .addGap(18, 18, 18)
                .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel28, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel23.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel23.setForeground(new java.awt.Color(255, 255, 255));
        jLabel23.setText("Equipamentos cadastrado");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel23, javax.swing.GroupLayout.DEFAULT_SIZE, 190, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(lblTotalEquipamentos, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(38, 38, 38)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jPanel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 31, Short.MAX_VALUE)
                .addComponent(lblTotalEquipamentos)
                .addGap(18, 18, 18)
                .addComponent(jLabel23)
                .addContainerGap())
        );

        jPanel3.setBackground(new java.awt.Color(9, 132, 227));
        jPanel3.setPreferredSize(new java.awt.Dimension(134, 125));

        lblValorTotal.setFont(new java.awt.Font("Tahoma", 1, 30)); // NOI18N
        lblValorTotal.setForeground(new java.awt.Color(255, 255, 255));
        lblValorTotal.setText(":");

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
                .addComponent(ValorTotalEquipamentos, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
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

        jLabel25.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel25.setForeground(new java.awt.Color(255, 255, 255));
        jLabel25.setText("Despesas com equipamentos");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel25, javax.swing.GroupLayout.DEFAULT_SIZE, 190, Short.MAX_VALUE)
                    .addComponent(lblValorTotal, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jPanel12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lblValorTotal)
                .addGap(18, 18, 18)
                .addComponent(jLabel25)
                .addContainerGap())
        );

        jPanel8.setBackground(new java.awt.Color(255, 0, 0));

        jLabel15.setBackground(new java.awt.Color(255, 255, 255));
        jLabel15.setFont(new java.awt.Font("Tahoma", 1, 30)); // NOI18N
        jLabel15.setForeground(new java.awt.Color(255, 255, 255));
        jLabel15.setText(":");

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
                .addComponent(jLabel14, javax.swing.GroupLayout.DEFAULT_SIZE, 141, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addContainerGap(13, Short.MAX_VALUE)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel27, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel14, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        jLabel24.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel24.setForeground(new java.awt.Color(255, 255, 255));
        jLabel24.setText("Equipamentos devolvidos");

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel24, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel15, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel8Layout.createSequentialGroup()
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel15)
                .addGap(18, 18, 18)
                .addComponent(jLabel24)
                .addContainerGap())
        );

        txtQuantidadePorTipo.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        txtQuantidadePorTipo.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtQuantidadePorTipo.setEnabled(false);

        jPanel1.setBackground(new java.awt.Color(9, 132, 227));

        jPanel7.setBackground(new java.awt.Color(116, 185, 255));

        jLabel16.setBackground(new java.awt.Color(255, 255, 255));
        jLabel16.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel16.setText("Nome Programa e Logo");

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel16, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(24, Short.MAX_VALUE))
        );

        jLabel18.setFont(new java.awt.Font("Cambria", 1, 14)); // NOI18N
        jLabel18.setForeground(new java.awt.Color(255, 255, 255));
        jLabel18.setText("Devolvidos");

        jLabel21.setFont(new java.awt.Font("Cambria", 1, 14)); // NOI18N
        jLabel21.setForeground(new java.awt.Color(255, 255, 255));
        jLabel21.setText("Custo");

        jLabel22.setFont(new java.awt.Font("Cambria", 1, 14)); // NOI18N
        jLabel22.setForeground(new java.awt.Color(255, 255, 255));
        jLabel22.setText("Ativos");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel18, javax.swing.GroupLayout.DEFAULT_SIZE, 160, Short.MAX_VALUE)
                    .addComponent(jLabel21, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel22, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel18)
                .addGap(18, 18, 18)
                .addComponent(jLabel21)
                .addGap(18, 18, 18)
                .addComponent(jLabel22)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        jLabel13.setFont(new java.awt.Font("Tahoma", 1, 36)); // NOI18N
        jLabel13.setForeground(new java.awt.Color(255, 255, 255));
        jLabel13.setText("Painel Dashboard");

        tblEquipamentos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Etiq. Equipamento", "Tipo", "Marca", "Setor", "Funcionário", "Valor", "Imagem", "Descrição"
            }
        ));
        tblEquipamentos.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblEquipamentosMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblEquipamentos);

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtQuantidadePorTipo, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane1)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(18, 18, 18)
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, 210, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(jLabel13)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(7, 7, 7)
                .addComponent(jLabel13)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, 165, Short.MAX_VALUE)
                    .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtQuantidadePorTipo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 279, Short.MAX_VALUE)
                .addGap(19, 19, 19))
        );

        jTabbedPane2.addTab("Dashboard", jPanel6);

        jPanel9.setBackground(new java.awt.Color(255, 255, 255));
        jPanel9.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        txtCondEqui.setColumns(20);
        txtCondEqui.setRows(5);
        txtCondEqui.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED, null, new java.awt.Color(204, 204, 204), null, null));
        jScrollPane2.setViewportView(txtCondEqui);

        jPanel9.add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 169, 538, 97));

        lblFotoPerfilEqui.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED, null, new java.awt.Color(204, 204, 204), null, null));
        jPanel9.add(lblFotoPerfilEqui, new org.netbeans.lib.awtextra.AbsoluteConstraints(698, 31, 171, 132));

        cboTipoEqui.setModel(new javax.swing.DefaultComboBoxModel(new String[] { " ", "Adaptador", "Desktop", "Monitor", "Leitor", "Mouse", "Teclado" }));
        jPanel9.add(cboTipoEqui, new org.netbeans.lib.awtextra.AbsoluteConstraints(136, 123, 128, -1));

        txtValorEqui.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED, null, new java.awt.Color(204, 204, 204), null, null));
        jPanel9.add(txtValorEqui, new org.netbeans.lib.awtextra.AbsoluteConstraints(416, 123, 132, -1));

        txtFuncionarioEqui.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED, null, new java.awt.Color(204, 204, 204), null, null));
        jPanel9.add(txtFuncionarioEqui, new org.netbeans.lib.awtextra.AbsoluteConstraints(558, 77, 122, -1));

        jLabel4.setText("Valor:");
        jPanel9.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(416, 103, -1, -1));

        jLabel2.setText("Tipo");
        jPanel9.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(136, 103, 120, -1));

        cboSetorEqui.setModel(new javax.swing.DefaultComboBoxModel(new String[] { " ", "Compras", "CFTV", "Frente de Loja", "Gerência", "Guarita", "Ilha", "Marketing", "Reunião", "RME", "Tesouraria", "TI" }));
        jPanel9.add(cboSetorEqui, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 123, 120, -1));

        jLabel5.setText("Setor");
        jPanel9.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 103, 120, -1));

        jLabel6.setText("Funcionário");
        jPanel9.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(558, 57, 122, -1));

        txtEtiquetaEqui.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED, null, new java.awt.Color(204, 204, 204), null, null));
        jPanel9.add(txtEtiquetaEqui, new org.netbeans.lib.awtextra.AbsoluteConstraints(136, 31, 128, -1));
        jPanel9.add(txtMarcaEqui, new org.netbeans.lib.awtextra.AbsoluteConstraints(274, 77, 132, -1));

        cboStatusEqui.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Ativo", "Reserva", "Devolvido" }));
        jPanel9.add(cboStatusEqui, new org.netbeans.lib.awtextra.AbsoluteConstraints(274, 123, 132, -1));

        jLabel10.setText("Condições do Equipamento");
        jPanel9.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 149, 146, -1));
        jPanel9.add(jDateChooser1, new org.netbeans.lib.awtextra.AbsoluteConstraints(136, 77, 128, -1));
        jPanel9.add(txtDataEquiCadastrado, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 77, 120, -1));

        jTextField2.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED, null, new java.awt.Color(204, 204, 204), null, null));
        jPanel9.add(jTextField2, new org.netbeans.lib.awtextra.AbsoluteConstraints(416, 77, 132, -1));

        txtDescricaoEqui.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED, null, new java.awt.Color(204, 204, 204), null, null));
        jPanel9.add(txtDescricaoEqui, new org.netbeans.lib.awtextra.AbsoluteConstraints(274, 31, 274, -1));

        jLabel1.setText("Nº. Etiqueta");
        jPanel9.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(136, 11, -1, -1));

        txtIdEqui.setEditable(false);
        txtIdEqui.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED, null, new java.awt.Color(204, 204, 204), null, null));
        txtIdEqui.setEnabled(false);
        txtIdEqui.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtIdEquiActionPerformed(evt);
            }
        });
        jPanel9.add(txtIdEqui, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 31, 52, -1));

        jLabel8.setText("Código");
        jPanel9.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 11, 52, -1));

        jLabel3.setText("Marca");
        jPanel9.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(274, 57, 60, -1));

        jLabel30.setText("Status");
        jPanel9.add(jLabel30, new org.netbeans.lib.awtextra.AbsoluteConstraints(274, 103, 132, -1));

        jLabel9.setText("Dt. de Saída");
        jPanel9.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(136, 57, 128, -1));

        jLabel11.setText("Dt. de Entrada");
        jPanel9.add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 57, 116, -1));

        jLabel29.setText("Quantidade:");
        jPanel9.add(jLabel29, new org.netbeans.lib.awtextra.AbsoluteConstraints(416, 57, -1, -1));

        jLabel7.setText("Descrição");
        jPanel9.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(274, 11, -1, -1));

        txtDiretorioImagemEqui.setEditable(false);
        txtDiretorioImagemEqui.setFont(new java.awt.Font("Tahoma", 0, 8)); // NOI18N
        txtDiretorioImagemEqui.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED, null, new java.awt.Color(204, 204, 204), null, null));
        jPanel9.add(txtDiretorioImagemEqui, new org.netbeans.lib.awtextra.AbsoluteConstraints(698, 169, 171, 20));

        jTextField3.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED, null, new java.awt.Color(204, 204, 204), null, null));
        jPanel9.add(jTextField3, new org.netbeans.lib.awtextra.AbsoluteConstraints(72, 31, 54, -1));

        jLabel31.setText("Filial");
        jPanel9.add(jLabel31, new org.netbeans.lib.awtextra.AbsoluteConstraints(72, 11, 54, -1));

        jTextField4.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED, null, new java.awt.Color(204, 204, 204), null, null));
        jPanel9.add(jTextField4, new org.netbeans.lib.awtextra.AbsoluteConstraints(558, 31, 122, -1));

        jLabel32.setText("Fornecedor");
        jPanel9.add(jLabel32, new org.netbeans.lib.awtextra.AbsoluteConstraints(558, 11, 122, -1));

        jPanel4.setBackground(new java.awt.Color(255, 255, 255));
        jPanel4.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));

        btnExcluirEqui.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        btnExcluirEqui.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/megacenter/icones/excluir.png"))); // NOI18N
        btnExcluirEqui.setText("  Excluir");
        btnExcluirEqui.setBorder(null);
        btnExcluirEqui.setMaximumSize(new java.awt.Dimension(63, 17));
        btnExcluirEqui.setMinimumSize(new java.awt.Dimension(63, 17));
        btnExcluirEqui.setPreferredSize(new java.awt.Dimension(81, 23));
        btnExcluirEqui.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExcluirEquiActionPerformed(evt);
            }
        });

        jButton1.setText("Consultar");
        jButton1.setMaximumSize(new java.awt.Dimension(63, 17));
        jButton1.setMinimumSize(new java.awt.Dimension(63, 17));

        btnAtualizarEqui.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
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

        btnCadastrarEqui.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        btnCadastrarEqui.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/megacenter/icones/adicionar.png"))); // NOI18N
        btnCadastrarEqui.setText("Cadastrar");
        btnCadastrarEqui.setToolTipText("Cadastrar");
        btnCadastrarEqui.setBorder(null);
        btnCadastrarEqui.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCadastrarEquiActionPerformed(evt);
            }
        });

        btnInserirImagemEqui.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/megacenter/icones/imagem.png"))); // NOI18N
        btnInserirImagemEqui.setText("Imagem");
        btnInserirImagemEqui.setBorder(null);
        btnInserirImagemEqui.setMaximumSize(new java.awt.Dimension(63, 17));
        btnInserirImagemEqui.setMinimumSize(new java.awt.Dimension(63, 17));
        btnInserirImagemEqui.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnInserirImagemEquiActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addGap(319, 319, 319)
                .addComponent(btnInserirImagemEqui, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnCadastrarEqui, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnAtualizarEqui, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, 88, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnExcluirEqui, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(btnExcluirEqui, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(btnAtualizarEqui, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(btnCadastrarEqui, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(btnInserirImagemEqui, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(6, 6, 6))
        );

        jPanel9.add(jPanel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 509, -1, -1));

        jLabel19.setText("Filtrar Nº Etiqueta");
        jPanel9.add(jLabel19, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 272, 146, -1));

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
        jPanel9.add(txtPesquisaEqui, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 292, 146, -1));

        tblEquipamentos3.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Etiq. Equipamento", "Tipo", "Marca", "Setor", "Funcionário", "Valor", "Imagem", "Descrição", "Data do Cadastro", "Cond. do Equipamento", "Status"
            }
        ));
        tblEquipamentos3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblEquipamentos3MouseClicked(evt);
            }
        });
        jScrollPane4.setViewportView(tblEquipamentos3);

        jPanel9.add(jScrollPane4, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 318, 859, 180));

        jLabel20.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/megacenter/icones/pesquisar.png"))); // NOI18N
        jPanel9.add(jLabel20, new org.netbeans.lib.awtextra.AbsoluteConstraints(162, 292, -1, 20));

        jLabel33.setText("Imagem");
        jPanel9.add(jLabel33, new org.netbeans.lib.awtextra.AbsoluteConstraints(698, 11, -1, -1));

        jTabbedPane2.addTab("Cadastro", jPanel9);

        getContentPane().add(jTabbedPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));

        getAccessibleContext().setAccessibleName("203 - Cadastro de Equipamento");

        setBounds(0, 0, 900, 616);
    }// </editor-fold>//GEN-END:initComponents

    private void tblEquipamentos3MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblEquipamentos3MouseClicked
        // chamando o metodo preencher campos
        exibir_campos();
    }//GEN-LAST:event_tblEquipamentos3MouseClicked

    private void txtPesquisaEquiKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtPesquisaEquiKeyReleased
        // chamando o metodo pesquisar equiámentos
        exibir_pesquisar_equipamento();
    }//GEN-LAST:event_txtPesquisaEquiKeyReleased

    private void txtPesquisaEquiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtPesquisaEquiActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtPesquisaEquiActionPerformed

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
        // Chamando o metodo alterar
        alterar_equipamento();
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


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel ValorTotalEquipamentos;
    private javax.swing.JButton btnAtualizarEqui;
    private javax.swing.JButton btnCadastrarEqui;
    private javax.swing.JButton btnExcluirEqui;
    private javax.swing.JButton btnInserirImagemEqui;
    private javax.swing.JComboBox<String> cboSetorEqui;
    private javax.swing.JComboBox cboStatusEqui;
    private javax.swing.JComboBox<String> cboTipoEqui;
    private javax.swing.JButton jButton1;
    private com.toedter.calendar.JDateChooser jDateChooser1;
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
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
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
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField3;
    private javax.swing.JTextField jTextField4;
    private javax.swing.JLabel lblFotoPerfilEqui;
    private javax.swing.JLabel lblTotalEquipamentos;
    private javax.swing.JLabel lblValorTotal;
    private javax.swing.JTable tblEquipamentos;
    private javax.swing.JTable tblEquipamentos3;
    private javax.swing.JTextArea txtCondEqui;
    private com.toedter.calendar.JDateChooser txtDataEquiCadastrado;
    private javax.swing.JTextField txtDescricaoEqui;
    private javax.swing.JTextField txtDiretorioImagemEqui;
    private javax.swing.JTextField txtEtiquetaEqui;
    private javax.swing.JTextField txtFuncionarioEqui;
    private javax.swing.JTextField txtIdEqui;
    private javax.swing.JTextField txtMarcaEqui;
    private javax.swing.JTextField txtPesquisaEqui;
    private javax.swing.JTextField txtQuantidadePorTipo;
    private javax.swing.JTextField txtValorEqui;
    // End of variables declaration//GEN-END:variables
}
