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
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.RowFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
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
        carregarEmpresasNoCombo();// chamando o metodo carregar empresas

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

    // criando o metodo para pesquisar os equipamentos pelo número da etiqueta
    private void pesquisar_equipamento() {
        String sql = "Select idequipamento as Código, etiqueta_equipamento as Etiqueta, data_cadastrado as Data, tipo as Tipo, marca as Marca, setor as Setor"
                + ", funcionario as Funcionário, valor as Valor, foto_equipamento as Imagem, descricao as Descrição, condicoes_equipamento as Condições, status as Status, id_filial as Filial,"
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

    // criando o metodo para pesquisar os equipamentos pelo nome do equipamento
    private void exibir_pesquisar_equipamento() {
        String sql = "Select idequipamento as Código, etiqueta_equipamento as Etiqueta, data_cadastrado as Data, tipo as Tipo, marca as Marca, setor as Setor"
                + ", funcionario as Funcionário, valor as Valor, foto_equipamento as Imagem, descricao as Descrição, condicoes_equipamento as Condições, status as Status, id_filial as Filial,"
                + "data_saida as 'Data de Saída', quantidade as Quantidade from equipamentos where etiqueta_equipamento "
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

    // criando o metodo para pesquisar os equipamentos pelo nome do equipamento
    private void filtro_pesquisar_equipamento() {
        String sql = "SELECT idequipamento AS Código, etiqueta_equipamento AS Etiqueta, data_cadastrado AS Data, tipo AS Tipo,"
                + " marca AS Marca, setor AS Setor, funcionario AS Funcionário, valor AS Valor, foto_equipamento AS Imagem,"
                + " condicoes_equipamento AS Condições, status AS Status, id_filial AS Filial, data_saida AS 'Data de Saída', quantidade AS Quantidade"
                + " FROM equipamentos WHERE descricao LIKE ?";

        try {
            pst = conexao.prepareStatement(sql);
            pst.setString(1, "%" + txtFiltroDescricaoEqui.getText() + "%"); // Busca em qualquer parte
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

    // Criando o metodo para preencher as culas da tabela de acordo com o banco de dados
    private void preencher_campos() {
        try {
            conexao = ModuloConexao.conector(); // Garante que está conectado ao banco

            int preencher = tblEquipamentos.getSelectedRow();
            if (preencher == -1) {
                return; // Sai sem fazer nada
            }

            // Preenchendo os campos de texto com tratamento para valores null
            txtIdEqui.setText(valorOuVazio(tblEquipamentos.getModel().getValueAt(preencher, 0)));
            txtEtiquetaEqui.setText(valorOuVazio(tblEquipamentos.getModel().getValueAt(preencher, 1)));
            // DATA: trate corretamente conforme o tipo vindo do model
            Object valorData = tblEquipamentos.getModel().getValueAt(preencher, 2);
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
            cboTipoEqui.setSelectedItem(valorOuVazio(tblEquipamentos.getModel().getValueAt(preencher, 3)));
            txtMarcaEqui.setText(valorOuVazio(tblEquipamentos.getModel().getValueAt(preencher, 4)));
            cboSetorEqui.setSelectedItem(valorOuVazio(tblEquipamentos.getModel().getValueAt(preencher, 5)));
            txtFuncionarioEqui.setText(valorOuVazio(tblEquipamentos.getModel().getValueAt(preencher, 6)));
            txtValorEqui.setText(valorOuVazio(tblEquipamentos.getModel().getValueAt(preencher, 7)));
            txtDescricaoEqui.setText(valorOuVazio(tblEquipamentos.getModel().getValueAt(preencher, 8)));
            txtCondEqui.setText(valorOuVazio(tblEquipamentos.getModel().getValueAt(preencher, 9)));
            cboStatusEqui.setSelectedItem(valorOuVazio(tblEquipamentos.getModel().getValueAt(preencher, 10)));
            cboFilialEqui.setSelectedItem(valorOuVazio(tblEquipamentos.getModel().getValueAt(preencher, 11)));
            
            // DATA: trate corretamente conforme o tipo vindo do model
            Object valorDataa = tblEquipamentos.getModel().getValueAt(preencher, 12);
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
            } else {
                dtSaidaEqui.setDate(null);
            }
            
            txtQuantidadeEqui.setText(valorOuVazio(tblEquipamentos.getModel().getValueAt(preencher, 13)));

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
            if (preencher == -1) {
                return;
            }

            // Preenchendo os campos de texto com tratamento para valores null
            txtIdEqui.setText(valorOuVazio(tblEquipamentos3.getModel().getValueAt(preencher, 0)));
            txtEtiquetaEqui.setText(valorOuVazio(tblEquipamentos3.getModel().getValueAt(preencher, 1)));
            // DATA: trate corretamente conforme o tipo vindo do model
            // ----------------------
            Object valorData = tblEquipamentos3.getModel().getValueAt(preencher, 2);
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
            cboTipoEqui.setSelectedItem(valorOuVazio(tblEquipamentos3.getModel().getValueAt(preencher, 3)));
            txtMarcaEqui.setText(valorOuVazio(tblEquipamentos3.getModel().getValueAt(preencher, 4)));
            cboSetorEqui.setSelectedItem(valorOuVazio(tblEquipamentos3.getModel().getValueAt(preencher, 5)));
            txtFuncionarioEqui.setText(valorOuVazio(tblEquipamentos3.getModel().getValueAt(preencher, 6)));
            txtValorEqui.setText(valorOuVazio(tblEquipamentos3.getModel().getValueAt(preencher, 7)));
            txtDiretorioImagemEqui.setText(valorOuVazio(tblEquipamentos3.getModel().getValueAt(preencher, 8)));
            txtDescricaoEqui.setText(valorOuVazio(tblEquipamentos3.getModel().getValueAt(preencher, 9)));
            txtCondEqui.setText(valorOuVazio(tblEquipamentos3.getModel().getValueAt(preencher, 10)));
            cboStatusEqui.setSelectedItem(valorOuVazio(tblEquipamentos3.getModel().getValueAt(preencher, 11)));
            txtQuantidadeEqui.setText(valorOuVazio(tblEquipamentos3.getModel().getValueAt(preencher, 12)));

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

    // A linha abaixo cria o método para cadastrar equipamentos
    private void cadastrar_equipamento() {
        String sql = "INSERT INTO equipamentos ("
                + "etiqueta_equipamento, tipo, Data_cadastrado, marca, setor, funcionario, valor, foto_equipamento, descricao, condicoes_equipamento, status, id_filial, data_saida,"
                + "quantidade"
                + ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        // Validação de campos obrigatórios
        if (txtEtiquetaEqui.getText().trim().isEmpty()
                || txtFuncionarioEqui.getText().trim().isEmpty()
                || cboSetorEqui.getSelectedItem() == null
                || cboTipoEqui.getSelectedItem() == null
                || cboSetorEqui.getSelectedItem().toString().trim().isEmpty()
                || cboTipoEqui.getSelectedItem().toString().trim().isEmpty()
                || dtEntradaEqui.getDate() == null) {

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
            java.util.Date utilDate = dtEntradaEqui.getDate();
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
            pst.setString(12, txtIdEqui.getText());         // idequipamento
            pst.setString(13, cboFilialEqui.getSelectedItem().toString());
            pst.setString(14, txtQuantidadeEqui.getText());

            // Executa inserção
            int adicionado = pst.executeUpdate();
            if (adicionado > 0) {
                JOptionPane.showMessageDialog(null, "Equipamento cadastrado com sucesso.");
                limpar_campos(); // limpando os dados após o cadastro
                exibir_todos_equipamentos_dashboard(); // chamando o metodo para atualizar os equipamentos no dashboard
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

            cboFilialEqui.removeAllItems(); // Limpa opções atuais
            cboFilialEqui.addItem("Selecione"); // opção padrão

            while (rs.next()) {
                String filial = rs.getString("codigo_filial");
                cboFilialEqui.addItem(filial);
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Erro ao carregar todas as filiais: " + e.getMessage());
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
        jLabel13 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblEquipamentos = new javax.swing.JTable();
        jPanel9 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        btnExcluirEqui = new javax.swing.JButton();
        btnConsultarEqui = new javax.swing.JButton();
        btnAtualizarEqui = new javax.swing.JButton();
        btnCadastrarEqui = new javax.swing.JButton();
        btnSalvarEqui = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        txtCondEqui = new javax.swing.JTextArea();
        jLabel10 = new javax.swing.JLabel();
        cboFornecedorEqui = new javax.swing.JComboBox();
        cboStatusEqui = new javax.swing.JComboBox();
        cboTipoEqui = new javax.swing.JComboBox<String>();
        cboSetorEqui = new javax.swing.JComboBox<String>();
        jLabel5 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel30 = new javax.swing.JLabel();
        jLabel32 = new javax.swing.JLabel();
        txtValorEqui = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        txtMarcaEqui = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        dtSaidaEqui = new com.toedter.calendar.JDateChooser();
        dtEntradaEqui = new com.toedter.calendar.JDateChooser();
        jLabel11 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        txtIdEqui = new javax.swing.JTextField();
        cboFilialEqui = new javax.swing.JComboBox();
        txtEtiquetaEqui = new javax.swing.JTextField();
        txtDescricaoEqui = new javax.swing.JTextField();
        txtFuncionarioEqui = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jLabel31 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        lblFotoPerfilEqui = new javax.swing.JLabel();
        txtDiretorioImagemEqui = new javax.swing.JTextField();
        btnExcluirImagemEqui = new javax.swing.JButton();
        btnAlterarImagemEqui = new javax.swing.JButton();
        btnInserirImagemEqui = new javax.swing.JButton();
        jLabel20 = new javax.swing.JLabel();
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
        bntBuscarEqui = new javax.swing.JButton();
        jScrollPane4 = new javax.swing.JScrollPane();
        tblEquipamentos3 = new javax.swing.JTable();
        txtQuantidadeEqui = new javax.swing.JTextField();

        jTextField1.setText("jTextField1");

        jLabel17.setText("jLabel17");

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setTitle("203 - Cadastrar Equipamento");
        setToolTipText("");
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel6.setBackground(new java.awt.Color(255, 255, 255));
        jPanel6.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel2.setBackground(new java.awt.Color(0, 184, 148));
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblTotalEquipamentos.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        lblTotalEquipamentos.setForeground(new java.awt.Color(255, 255, 255));
        lblTotalEquipamentos.setText(":");
        jPanel2.add(lblTotalEquipamentos, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 60, 202, -1));

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
                .addContainerGap(114, Short.MAX_VALUE))
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

        jPanel2.add(jPanel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 260, -1));

        jLabel23.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel23.setForeground(new java.awt.Color(255, 255, 255));
        jLabel23.setText("Equipamentos cadastrado");
        jPanel2.add(jLabel23, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 107, 240, -1));

        jPanel6.add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 41, -1, 130));

        jPanel3.setBackground(new java.awt.Color(9, 132, 227));
        jPanel3.setPreferredSize(new java.awt.Dimension(134, 125));
        jPanel3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblValorTotal.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        lblValorTotal.setForeground(new java.awt.Color(255, 255, 255));
        lblValorTotal.setText(":");
        jPanel3.add(lblValorTotal, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 61, 240, -1));

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
                .addComponent(ValorTotalEquipamentos, javax.swing.GroupLayout.DEFAULT_SIZE, 198, Short.MAX_VALUE)
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

        jPanel3.add(jPanel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 260, -1));

        jLabel25.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel25.setForeground(new java.awt.Color(255, 255, 255));
        jLabel25.setText("Despesas com equipamentos");
        jPanel3.add(jLabel25, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 108, 240, -1));

        jPanel6.add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(605, 41, 260, 130));

        jPanel8.setBackground(new java.awt.Color(255, 0, 0));
        jPanel8.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel15.setBackground(new java.awt.Color(255, 255, 255));
        jLabel15.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabel15.setForeground(new java.awt.Color(255, 255, 255));
        jLabel15.setText(":");
        jPanel8.add(jLabel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 62, 240, -1));

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
                .addComponent(jLabel14, javax.swing.GroupLayout.DEFAULT_SIZE, 190, Short.MAX_VALUE)
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

        jPanel8.add(jPanel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));

        jLabel24.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel24.setForeground(new java.awt.Color(255, 255, 255));
        jLabel24.setText("Equipamentos devolvidos");
        jPanel8.add(jLabel24, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 109, 240, -1));

        jPanel6.add(jPanel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(308, 41, -1, 130));

        txtQuantidadePorTipo.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        txtQuantidadePorTipo.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtQuantidadePorTipo.setEnabled(false);
        jPanel6.add(txtQuantidadePorTipo, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 183, 855, -1));

        jLabel13.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabel13.setText("Painel Dashboard");
        jPanel6.add(jLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 6, -1, -1));

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

        jPanel6.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 210, 855, 390));

        jTabbedPane2.addTab("Dashboard", jPanel6);

        jPanel9.setBackground(new java.awt.Color(255, 255, 255));

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

        btnConsultarEqui.setText("Consultar");
        btnConsultarEqui.setMaximumSize(new java.awt.Dimension(63, 17));
        btnConsultarEqui.setMinimumSize(new java.awt.Dimension(63, 17));
        btnConsultarEqui.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnConsultarEquiActionPerformed(evt);
            }
        });

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

        btnSalvarEqui.setText("Salvar");
        btnSalvarEqui.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSalvarEquiActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addGap(226, 226, 226)
                .addComponent(btnSalvarEqui, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(113, 113, 113)
                .addComponent(btnCadastrarEqui, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnAtualizarEqui, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnConsultarEqui, javax.swing.GroupLayout.DEFAULT_SIZE, 84, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnExcluirEqui, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnConsultarEqui, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(btnSalvarEqui, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnExcluirEqui, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(btnAtualizarEqui, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(btnCadastrarEqui, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addGap(6, 6, 6))
        );

        txtCondEqui.setColumns(20);
        txtCondEqui.setRows(5);
        txtCondEqui.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED, null, new java.awt.Color(204, 204, 204), null, null));
        jScrollPane2.setViewportView(txtCondEqui);

        jLabel10.setText("Condições do Equipamento");

        cboStatusEqui.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Ativo", "Reserva", "Devolvido" }));

        cboTipoEqui.setModel(new javax.swing.DefaultComboBoxModel(new String[] { " ", "Adaptador", "Desktop", "Monitor", "Leitor", "Mouse", "Teclado" }));

        cboSetorEqui.setModel(new javax.swing.DefaultComboBoxModel(new String[] { " ", "Compras", "CFTV", "Frente de Loja", "Gerência", "Guarita", "Ilha", "Marketing", "Reunião", "RME", "Tesouraria", "TI" }));

        jLabel5.setText("Setor");

        jLabel2.setText("Tipo");

        jLabel30.setText("Status");

        jLabel32.setText("Fornecedor");

        txtValorEqui.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED, null, new java.awt.Color(204, 204, 204), null, null));

        jLabel4.setText("Valor:");

        jLabel29.setText("Quantidade:");

        jLabel3.setText("Marca");

        jLabel11.setText("Dt. de Entrada");

        jLabel9.setText("Dt. de Saída");

        txtIdEqui.setEditable(false);
        txtIdEqui.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED, null, new java.awt.Color(204, 204, 204), null, null));
        txtIdEqui.setEnabled(false);
        txtIdEqui.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtIdEquiActionPerformed(evt);
            }
        });

        cboFilialEqui.setModel(new javax.swing.DefaultComboBoxModel(new String[] { " " }));

        txtEtiquetaEqui.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED, null, new java.awt.Color(204, 204, 204), null, null));

        txtDescricaoEqui.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED, null, new java.awt.Color(204, 204, 204), null, null));

        txtFuncionarioEqui.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED, null, new java.awt.Color(204, 204, 204), null, null));

        jLabel6.setText("Funcionário");

        jLabel7.setText("Descrição");

        jLabel1.setText("Nº. Etiqueta");

        jLabel31.setText("Filial");

        jLabel8.setText("Código");

        lblFotoPerfilEqui.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED, null, new java.awt.Color(204, 204, 204), null, null));

        txtDiretorioImagemEqui.setEditable(false);
        txtDiretorioImagemEqui.setBackground(new java.awt.Color(255, 255, 255));
        txtDiretorioImagemEqui.setFont(new java.awt.Font("Tahoma", 0, 3)); // NOI18N
        txtDiretorioImagemEqui.setForeground(new java.awt.Color(255, 255, 255));
        txtDiretorioImagemEqui.setBorder(null);

        btnExcluirImagemEqui.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        btnExcluirImagemEqui.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/megacenter/icones/imagem.png"))); // NOI18N
        btnExcluirImagemEqui.setText("Excluir");
        btnExcluirImagemEqui.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExcluirImagemEquiActionPerformed(evt);
            }
        });

        btnAlterarImagemEqui.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        btnAlterarImagemEqui.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/megacenter/icones/imagem.png"))); // NOI18N
        btnAlterarImagemEqui.setText("Alterar");
        btnAlterarImagemEqui.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAlterarImagemEquiActionPerformed(evt);
            }
        });

        btnInserirImagemEqui.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        btnInserirImagemEqui.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/megacenter/icones/imagem.png"))); // NOI18N
        btnInserirImagemEqui.setText("Adicionar");
        btnInserirImagemEqui.setBorder(null);
        btnInserirImagemEqui.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnInserirImagemEquiActionPerformed(evt);
            }
        });

        jLabel20.setText("Imagem");

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));

        jLabel19.setText("Nº Etiqueta");

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

        bntBuscarEqui.setBackground(new java.awt.Color(255, 255, 204));
        bntBuscarEqui.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/megacenter/icones/pesquisar.png"))); // NOI18N
        bntBuscarEqui.setText("Buscar");
        bntBuscarEqui.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bntBuscarEquiActionPerformed(evt);
            }
        });

        tblEquipamentos3.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Nº Etiqueta", "Filial", "Tipo", "Descrição", "Setor", "Funcionário", "Fornecedor", "Valor", "Data Entrada", "Data Saída", "Status", "Quantidade"
            }
        ));
        tblEquipamentos3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblEquipamentos3MouseClicked(evt);
            }
        });
        jScrollPane4.setViewportView(tblEquipamentos3);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane4)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(txtPesquisaEqui, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cboFiltroFilialEqui, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtFiltroDescricaoEqui, javax.swing.GroupLayout.PREFERRED_SIZE, 223, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(cboFiltroTipoEqui, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel22, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(cboFiltroUsuarioEqui, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(bntBuscarEqui, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel19)
                    .addComponent(jLabel18)
                    .addComponent(jLabel16)
                    .addComponent(jLabel21)
                    .addComponent(jLabel22))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtPesquisaEqui, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cboFiltroFilialEqui, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtFiltroDescricaoEqui, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cboFiltroTipoEqui, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cboFiltroUsuarioEqui, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(bntBuscarEqui, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 210, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel9Layout.createSequentialGroup()
                        .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel9Layout.createSequentialGroup()
                                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel9Layout.createSequentialGroup()
                                        .addComponent(txtIdEqui, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(cboFilialEqui, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(txtEtiquetaEqui, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanel9Layout.createSequentialGroup()
                                        .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(10, 10, 10)
                                        .addComponent(jLabel31, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(10, 10, 10)
                                        .addComponent(jLabel1)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel7)
                                    .addComponent(txtDescricaoEqui)))
                            .addGroup(jPanel9Layout.createSequentialGroup()
                                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel9Layout.createSequentialGroup()
                                        .addComponent(cboSetorEqui, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(cboTipoEqui, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanel9Layout.createSequentialGroup()
                                        .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(76, 76, 76)
                                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel9Layout.createSequentialGroup()
                                        .addComponent(cboStatusEqui, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(cboFornecedorEqui, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanel9Layout.createSequentialGroup()
                                        .addComponent(jLabel30, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel32, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addGroup(jPanel9Layout.createSequentialGroup()
                                                .addComponent(txtQuantidadeEqui, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(10, 10, 10)
                                                .addComponent(txtValorEqui, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE))))))
                            .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 146, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel9Layout.createSequentialGroup()
                                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addGroup(jPanel9Layout.createSequentialGroup()
                                        .addComponent(dtEntradaEqui, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(6, 6, 6)
                                        .addComponent(dtSaidaEqui, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(txtMarcaEqui, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(82, 82, 82))
                                    .addGroup(jPanel9Layout.createSequentialGroup()
                                        .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(36, 36, 36)
                                        .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(48, 48, 48)
                                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jLabel29, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)))
                                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel9Layout.createSequentialGroup()
                                .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(jPanel9Layout.createSequentialGroup()
                                .addComponent(txtFuncionarioEqui)
                                .addGap(11, 11, 11)))
                        .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(txtDiretorioImagemEqui, javax.swing.GroupLayout.PREFERRED_SIZE, 167, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel20)
                                .addComponent(lblFotoPerfilEqui, javax.swing.GroupLayout.PREFERRED_SIZE, 167, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(5, 5, 5))
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 534, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel9Layout.createSequentialGroup()
                                .addComponent(btnInserirImagemEqui, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnAlterarImagemEqui))
                            .addComponent(btnExcluirImagemEqui, javax.swing.GroupLayout.Alignment.TRAILING))))
                .addContainerGap())
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addGap(11, 11, 11)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel8)
                    .addComponent(jLabel31)
                    .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel1)
                        .addComponent(jLabel7)
                        .addComponent(jLabel6)
                        .addComponent(jLabel20)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addComponent(lblFotoPerfilEqui, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtDiretorioImagemEqui, javax.swing.GroupLayout.PREFERRED_SIZE, 5, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnInserirImagemEqui, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnAlterarImagemEqui, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnExcluirImagemEqui, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtIdEqui, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(txtEtiquetaEqui, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(cboFilialEqui, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(txtDescricaoEqui, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(txtFuncionarioEqui, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel3)
                                .addComponent(jLabel29)
                                .addComponent(jLabel4))
                            .addComponent(jLabel11)
                            .addComponent(jLabel9))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(txtMarcaEqui, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(txtQuantidadeEqui, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(txtValorEqui, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(dtEntradaEqui, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(dtSaidaEqui, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel5)
                                .addComponent(jLabel2))
                            .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel30)
                                .addComponent(jLabel32)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(cboStatusEqui, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(cboFornecedorEqui, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(cboSetorEqui, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(cboTipoEqui, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel10)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(6, 6, 6))
        );

        jTabbedPane2.addTab("Cadastro", jPanel9);

        getContentPane().add(jTabbedPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(4, 0, 880, 640));

        getAccessibleContext().setAccessibleName("203 - Cadastro de Equipamento");

        setBounds(0, 0, 905, 671);
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


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel ValorTotalEquipamentos;
    private javax.swing.JButton bntBuscarEqui;
    private javax.swing.JButton btnAlterarImagemEqui;
    private javax.swing.JButton btnAtualizarEqui;
    private javax.swing.JButton btnCadastrarEqui;
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
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JTabbedPane jTabbedPane2;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JLabel lblFotoPerfilEqui;
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
