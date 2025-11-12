/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JInternalFrame.java to edit this template
 */
package br.com.megacenter.screens;

import br.com.megacenter.dal.ModuloConexao;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import net.proteanit.sql.DbUtils;

/**
 *
 * @author JANDERSON
 */
public class ScreenEmpresa extends javax.swing.JInternalFrame {

    Connection conexao = null;
    PreparedStatement pst = null;
    ResultSet rs = null;

    /**
     * Creates new form ScreenEmpresa
     */
    public ScreenEmpresa() {
        initComponents();
        conexao = ModuloConexao.conector();
    }

    // cirando o metodo cadastrar
    private void cadastrar() {
        String sql = "insert into empresas(razao_social, cnpj, email, telefone, "
                + "endereco, complemento, numero, bairro, cidade, cep, uf) values(?,?,?,?,?,?,?,?,?,?,?)";
        try {
            pst = conexao.prepareStatement(sql);
            pst.setString(1, txtRazaoSocialEmpre.getText());
            pst.setString(2, txtCnpjEmpre.getText());
            pst.setString(3, txtEmailEmpre.getText());
            pst.setString(4, txtTelefoneEmpre.getText());
            pst.setString(5, txtEnderecoEmpre.getText());
            pst.setString(6, txtComplementoEmpre.getText());
            pst.setString(7, txtNumeroEmpre.getText());
            pst.setString(8, txtBairroEmpre.getText());
            pst.setString(9, txtCidadeEmpre.getText());
            pst.setString(10, txtCepEmpre.getText());
            pst.setString(11, txtUfEmpre.getText());
            // criando a validação dos campos obrigatórios
            if ((txtRazaoSocialEmpre.getText().isEmpty()) || (txtCnpjEmpre.getText().isEmpty()) || (txtTelefoneEmpre.getText().isEmpty())
                    || (txtEmailEmpre.getText().isEmpty())) {
                JOptionPane.showMessageDialog(null, "Por favor, preencha todos os campos obrigatórios!");
            } else {
                // a linha abaixo atualiza a tabela usuarios com os dados do formulário
                // a estrutura abaixo é uasa para confirmar a alteração dos dados na tabela
                int cadastrado = pst.executeUpdate();
                // a linha abaixo serve de apoio a lógica
                //System.out.println(cadastrado);
                if (cadastrado > 0) {
                    JOptionPane.showMessageDialog(null, "Empresa cadastrada com sucesso!");
                    limpar();
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }

    // criando o metodo de pesquisa aavançada utilizando o nome das empresas no banco de dados
    private void pesquisar_empresa() {
        String sql = " select idempre as Código, razao_social as Nome, cnpj as CNPJ, email as Email, telefone as Telefone,"
                + "Endereco as Endereço, complemento as Complemento, numero as Número, bairro as Bairro, cidade as Cidade,"
                + "cep as CEP, uf as UF from empresas where razao_social like ?";
        try {
            pst = conexao.prepareStatement(sql);
            // a linha seguir, passa o conteudo da caixa de pesquisa para o ?
            // atenção ao "%" - continuação da String sql
            pst.setString(1, txtPesquisarEmpre.getText() + "%");
            rs = pst.executeQuery();
            // a linha abaixo usa a biblioteca rs2xml.jar para preencher a tabela
            tblEmpresas.setModel(DbUtils.resultSetToTableModel(rs));

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }

    // criando o metodo para preencher os campos do formulário com o conteúdo da tabela
    public void preencher_campos() {
        int preencher = tblEmpresas.getSelectedRow();
        txtIdEmpre.setText(tblEmpresas.getModel().getValueAt(preencher, 0).toString());
        txtRazaoSocialEmpre.setText(tblEmpresas.getModel().getValueAt(preencher, 1).toString());
        txtCnpjEmpre.setText(tblEmpresas.getModel().getValueAt(preencher, 2).toString());
        txtEmailEmpre.setText(tblEmpresas.getModel().getValueAt(preencher, 3).toString());
        txtTelefoneEmpre.setText(tblEmpresas.getModel().getValueAt(preencher, 4).toString());
        txtEnderecoEmpre.setText(tblEmpresas.getModel().getValueAt(preencher, 5).toString());
        txtComplementoEmpre.setText(tblEmpresas.getModel().getValueAt(preencher, 6).toString());
        txtNumeroEmpre.setText(tblEmpresas.getModel().getValueAt(preencher, 7).toString());
        txtBairroEmpre.setText(tblEmpresas.getModel().getValueAt(preencher, 8).toString());
        txtCidadeEmpre.setText(tblEmpresas.getModel().getValueAt(preencher, 9).toString());
        txtCepEmpre.setText(tblEmpresas.getModel().getValueAt(preencher, 10).toString());
        txtUfEmpre.setText(tblEmpresas.getModel().getValueAt(preencher, 11).toString());

// a linha abaixo vai desativar o botão de cadastrar
        btnCadastrar.setEnabled(false);
    }

    // criando o metodo para altar os dados da empresa
    // criando o metodo para alterar os dados do usuário no banco de dados
    private void alterar() {
        String sql = "UPDATE empresas SET razao_social=?, cnpj=?, email=?, telefone=?, "
                + "endereco=?, complemento=?, numero=?, bairro=?, cidade=?, cep=?, uf=? WHERE idempre=?";
        try {
            pst = conexao.prepareStatement(sql);

            // Definição dos 11 primeiros parâmetros
            pst.setString(1, txtRazaoSocialEmpre.getText());
            pst.setString(2, txtCnpjEmpre.getText());
            pst.setString(3, txtEmailEmpre.getText());
            pst.setString(4, txtTelefoneEmpre.getText());
            pst.setString(5, txtEnderecoEmpre.getText());
            pst.setString(6, txtComplementoEmpre.getText());
            pst.setString(7, txtNumeroEmpre.getText());
            pst.setString(8, txtBairroEmpre.getText());
            pst.setString(9, txtCidadeEmpre.getText());
            pst.setString(10, txtCepEmpre.getText());
            pst.setString(11, txtUfEmpre.getText());
            pst.setString(12, txtIdEmpre.getText());

            // Validação de campos obrigatórios
            if (txtRazaoSocialEmpre.getText().isEmpty() || txtCnpjEmpre.getText().isEmpty()
                    || txtTelefoneEmpre.getText().isEmpty() || txtEmailEmpre.getText().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Por favor, preencha todos os campos obrigatórios!");
            } else {
                int atualizado = pst.executeUpdate();
                if (atualizado > 0) {
                    JOptionPane.showMessageDialog(null, "Empresa atualizada com sucesso!");
                    // chamando o metodo limpar
                    limpar();

                    // a linha abaixo vai reabilitar o botão cadastrar após alterar o dados de uma empresa
                    btnCadastrar.setEnabled(true);
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Erro ao atualizar empresa: " + e.getMessage());
        }
    }

    // criando o metodo responsável pela exlusão de uma empresa no banco de dados
    private void excluir() {
        // a estrutura abaxio confirma a exclusão do usuário
        int confirma = JOptionPane.showConfirmDialog(null, "Você tem certeza que deseja excluir esta empresa do sistema?", "Atenção", JOptionPane.YES_NO_OPTION);
        if (confirma == JOptionPane.YES_OPTION) {
            String sql = "delete from empresas where idempre=?";
            try {
                pst = conexao.prepareStatement(sql);
                pst.setString(1, txtIdEmpre.getText());
                int excluido = pst.executeUpdate();
                if (excluido > 0) {
                    JOptionPane.showMessageDialog(null, "Empresa excluida com sucesso!");
                        // chamando o metodo limpar
                    limpar();

                    // a linha abaixo vai reabilitar o botão cadastrar após alterar o dados de uma empresa
                    btnCadastrar.setEnabled(true);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, e);
            }
        }
    }

    // metodo para limpar os campos do formulário e a tabela
    private void limpar() {
        txtPesquisarEmpre.setText(null);
        txtIdEmpre.setText(null);
        txtRazaoSocialEmpre.setText(null);
        txtCnpjEmpre.setText(null);
        txtEnderecoEmpre.setText(null);
        txtNumeroEmpre.setText(null);
        txtComplementoEmpre.setText(null);
        txtBairroEmpre.setText(null);
        txtCidadeEmpre.setText(null);
        txtCepEmpre.setText(null);
        txtUfEmpre.setText(null);
        txtTelefoneEmpre.setText(null);
        txtEmailEmpre.setText(null);
        ((DefaultTableModel) tblEmpresas.getModel()).setRowCount(0);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTabbedPane2 = new javax.swing.JTabbedPane();
        jPanel2 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        txtPesquisarEmpre = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblEmpresas = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        txtCnpjEmpre = new javax.swing.JTextField();
        lblTelefoneEmpre = new javax.swing.JLabel();
        txtTelefoneEmpre = new javax.swing.JTextField();
        txtNumeroEmpre = new javax.swing.JTextField();
        txtCidadeEmpre = new javax.swing.JTextField();
        lblNumeroEmpre = new javax.swing.JLabel();
        txtUfEmpre = new javax.swing.JTextField();
        lblEnderecoEmpre = new javax.swing.JLabel();
        lblCnpjEmpre = new javax.swing.JLabel();
        txtCepEmpre = new javax.swing.JTextField();
        lblEmailEmpre = new javax.swing.JLabel();
        lblRazaoSocialEmpre = new javax.swing.JLabel();
        lblCepEmpre = new javax.swing.JLabel();
        lblComplementoEmpre = new javax.swing.JLabel();
        txtRazaoSocialEmpre = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        txtIdEmpre = new javax.swing.JTextField();
        txtBairroEmpre = new javax.swing.JTextField();
        txtEnderecoEmpre = new javax.swing.JTextField();
        lblCidadeEmpre = new javax.swing.JLabel();
        lblBairroEmpre = new javax.swing.JLabel();
        lblUfEmpre = new javax.swing.JLabel();
        txtComplementoEmpre = new javax.swing.JTextField();
        txtEmailEmpre = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        btnCadastrar = new javax.swing.JButton();
        btnAtualizar = new javax.swing.JButton();
        btnExcluir = new javax.swing.JButton();

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);

        jPanel2.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED, java.awt.Color.white, java.awt.Color.white, java.awt.Color.white, java.awt.Color.white));

        jLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/megacenter/icones/pesquisar.png"))); // NOI18N

        txtPesquisarEmpre.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtPesquisarEmpreKeyReleased(evt);
            }
        });

        tblEmpresas = new javax.swing.JTable() {
            public boolean isCellEditable(int rowIndex, int ColIndex){
                return false;
            }
        };
        tblEmpresas.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null}
            },
            new String [] {
                "Código", "R. Social", "CNPJ", "Email", "Telefone", "Endereço", "Complemento", "Número", "Bairro", "Cidade", "CEP", "UF"
            }
        ));
        tblEmpresas.setFocusable(false);
        tblEmpresas.getTableHeader().setReorderingAllowed(false);
        tblEmpresas.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblEmpresasMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblEmpresas);

        lblTelefoneEmpre.setText("*Telefone:");

        txtNumeroEmpre.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtNumeroEmpreActionPerformed(evt);
            }
        });

        lblNumeroEmpre.setText("Número:");

        lblEnderecoEmpre.setText("Endereço:");

        lblCnpjEmpre.setText("*CNPJ:");

        lblEmailEmpre.setText("*E-mail:");

        lblRazaoSocialEmpre.setText("*Razação Social:");

        lblCepEmpre.setText("CEP:");

        lblComplementoEmpre.setText("Complemento:");

        jLabel1.setText("Cód. da Empresa:");

        txtIdEmpre.setEnabled(false);

        lblCidadeEmpre.setText("Cidade:");

        lblBairroEmpre.setText("Bairro:");

        lblUfEmpre.setText("UF:");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addComponent(txtTelefoneEmpre, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(10, 10, 10)
                            .addComponent(txtEmailEmpre))
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addComponent(txtBairroEmpre, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(6, 6, 6)
                            .addComponent(txtCidadeEmpre, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(6, 6, 6)
                            .addComponent(txtCepEmpre, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(6, 6, 6)
                            .addComponent(txtUfEmpre, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(6, 6, 6)
                        .addComponent(lblRazaoSocialEmpre)
                        .addGap(251, 251, 251)
                        .addComponent(lblCnpjEmpre))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(lblEnderecoEmpre)
                        .addGap(310, 310, 310)
                        .addComponent(lblNumeroEmpre)
                        .addGap(24, 24, 24)
                        .addComponent(lblComplementoEmpre))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(txtEnderecoEmpre, javax.swing.GroupLayout.PREFERRED_SIZE, 353, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(6, 6, 6)
                        .addComponent(txtNumeroEmpre, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(6, 6, 6)
                        .addComponent(txtComplementoEmpre, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(lblBairroEmpre)
                        .addGap(59, 59, 59)
                        .addComponent(lblCidadeEmpre)
                        .addGap(108, 108, 108)
                        .addComponent(lblCepEmpre)
                        .addGap(100, 100, 100)
                        .addComponent(lblUfEmpre))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(lblTelefoneEmpre)
                        .addGap(98, 98, 98)
                        .addComponent(lblEmailEmpre))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(txtIdEmpre, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(6, 6, 6)
                        .addComponent(txtRazaoSocialEmpre, javax.swing.GroupLayout.PREFERRED_SIZE, 326, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(6, 6, 6)
                        .addComponent(txtCnpjEmpre, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(177, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addComponent(lblRazaoSocialEmpre)
                    .addComponent(lblCnpjEmpre))
                .addGap(5, 5, 5)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtIdEmpre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtRazaoSocialEmpre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtCnpjEmpre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(6, 6, 6)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblEnderecoEmpre)
                    .addComponent(lblNumeroEmpre)
                    .addComponent(lblComplementoEmpre))
                .addGap(6, 6, 6)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtEnderecoEmpre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtNumeroEmpre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtComplementoEmpre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(6, 6, 6)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblBairroEmpre)
                    .addComponent(lblCidadeEmpre)
                    .addComponent(lblCepEmpre)
                    .addComponent(lblUfEmpre))
                .addGap(6, 6, 6)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtBairroEmpre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtCidadeEmpre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtCepEmpre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtUfEmpre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(6, 6, 6)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblTelefoneEmpre)
                    .addComponent(lblEmailEmpre))
                .addGap(6, 6, 6)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtTelefoneEmpre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtEmailEmpre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel2.setBackground(new java.awt.Color(255, 51, 0));
        jLabel2.setForeground(new java.awt.Color(255, 51, 0));
        jLabel2.setText("* Campos obrigatórios");

        jPanel3.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255)));
        jPanel3.setPreferredSize(new java.awt.Dimension(269, 30));

        btnCadastrar.setText("Cadastrar");
        btnCadastrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCadastrarActionPerformed(evt);
            }
        });

        btnAtualizar.setText("Atualizar");
        btnAtualizar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAtualizarActionPerformed(evt);
            }
        });

        btnExcluir.setText("Excluir");
        btnExcluir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExcluirActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(2, 2, 2)
                .addComponent(btnCadastrar)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnAtualizar, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnExcluir, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(btnAtualizar)
                .addComponent(btnCadastrar)
                .addComponent(btnExcluir))
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(txtPesquisarEmpre, javax.swing.GroupLayout.PREFERRED_SIZE, 295, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(4, 4, 4)
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel2))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtPesquisarEmpre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addGap(9, 9, 9)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 52, Short.MAX_VALUE)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jTabbedPane2.addTab("Cadastro", jPanel2);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane2)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane2)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtPesquisarEmpreKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtPesquisarEmpreKeyReleased
        // chamando o metodo pesquisar empresas
        pesquisar_empresa();
    }//GEN-LAST:event_txtPesquisarEmpreKeyReleased

    private void tblEmpresasMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblEmpresasMouseClicked
        // Chamando o metodo preencher
        preencher_campos();
    }//GEN-LAST:event_tblEmpresasMouseClicked

    private void txtNumeroEmpreActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtNumeroEmpreActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtNumeroEmpreActionPerformed

    private void btnCadastrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCadastrarActionPerformed
        // Chamando o metodo cadastrar empresa
        cadastrar();
    }//GEN-LAST:event_btnCadastrarActionPerformed

    private void btnAtualizarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAtualizarActionPerformed
        // chamando o metodo alterar
        alterar();
    }//GEN-LAST:event_btnAtualizarActionPerformed

    private void btnExcluirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExcluirActionPerformed
        // Chamando o metodo excluir
        excluir();
    }//GEN-LAST:event_btnExcluirActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAtualizar;
    private javax.swing.JButton btnCadastrar;
    private javax.swing.JButton btnExcluir;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTabbedPane jTabbedPane2;
    private javax.swing.JLabel lblBairroEmpre;
    private javax.swing.JLabel lblCepEmpre;
    private javax.swing.JLabel lblCidadeEmpre;
    private javax.swing.JLabel lblCnpjEmpre;
    private javax.swing.JLabel lblComplementoEmpre;
    private javax.swing.JLabel lblEmailEmpre;
    private javax.swing.JLabel lblEnderecoEmpre;
    private javax.swing.JLabel lblNumeroEmpre;
    private javax.swing.JLabel lblRazaoSocialEmpre;
    private javax.swing.JLabel lblTelefoneEmpre;
    private javax.swing.JLabel lblUfEmpre;
    private javax.swing.JTable tblEmpresas;
    private javax.swing.JTextField txtBairroEmpre;
    private javax.swing.JTextField txtCepEmpre;
    private javax.swing.JTextField txtCidadeEmpre;
    private javax.swing.JTextField txtCnpjEmpre;
    private javax.swing.JTextField txtComplementoEmpre;
    private javax.swing.JTextField txtEmailEmpre;
    private javax.swing.JTextField txtEnderecoEmpre;
    private javax.swing.JTextField txtIdEmpre;
    private javax.swing.JTextField txtNumeroEmpre;
    private javax.swing.JTextField txtPesquisarEmpre;
    private javax.swing.JTextField txtRazaoSocialEmpre;
    private javax.swing.JTextField txtTelefoneEmpre;
    private javax.swing.JTextField txtUfEmpre;
    // End of variables declaration//GEN-END:variables
}
