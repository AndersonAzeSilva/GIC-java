/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.megacenter.screens;

import br.com.megacenter.dal.ModuloConexao;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.ParseException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import net.proteanit.sql.DbUtils;

/**
 *
 * @author JANDERSON
 */
public class ScreenFilial extends javax.swing.JInternalFrame {

    Connection conexao = null;
    PreparedStatement pst = null;
    ResultSet rs = null;

    /**
     * Creates new form ScrenFilial
     */
    public ScreenFilial() {
        initComponents();
        conexao = ModuloConexao.conector();
    }

    // cirando o metodo cadastrar
    private void cadastrar() {
        String sql = "insert into filial(codigo_filial, nome_filial, cnpj_filial) values(?,?,?)";
        try {
            pst = conexao.prepareStatement(sql);
            pst.setString(1, txtCodigoFilial.getText());
            pst.setString(2, txtNomeFilial.getText());
            pst.setString(3, txtCnpjFilial.getText());
            
            // criando a validação dos campos obrigatórios
            if ((txtCodigoFilial.getText().isEmpty()) || (txtNomeFilial.getText().isEmpty()) || (txtCnpjFilial.getText().isEmpty())) {
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
    private void pesquisar_filial() {
        String sql = " select id_filial as 'Código', codigo_filial as 'Código Filial', nome_filial as 'Nome', "
                + "cnpj_filial as 'CNPJ' from filial where nome_filial like ?";
        try {
            pst = conexao.prepareStatement(sql);
            // a linha seguir, passa o conteudo da caixa de pesquisa para o ?
            // atenção ao "%" - continuação da String sql
            pst.setString(1, txtPesquisarFilial.getText() + "%");
            rs = pst.executeQuery();
            // a linha abaixo usa a biblioteca rs2xml.jar para preencher a tabela
            tblFilial.setModel(DbUtils.resultSetToTableModel(rs));

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }

    // criando o metodo para preencher os campos do formulário com o conteúdo da tabela
    public void preencher_campos() {
        int preencher = tblFilial.getSelectedRow();
        txtIdFilial.setText(tblFilial.getModel().getValueAt(preencher, 0).toString());
        txtCodigoFilial.setText(tblFilial.getModel().getValueAt(preencher, 1).toString());
        txtNomeFilial.setText(tblFilial.getModel().getValueAt(preencher, 2).toString());
        txtCnpjFilial.setText(tblFilial.getModel().getValueAt(preencher, 3).toString());
        // a linha abaixo vai desativar o botão de cadastrar
        btnCadastrar.setEnabled(false);
    }

    // criando o metodo para altar os dados da empresa
    // criando o metodo para alterar os dados do usuário no banco de dados
    private void alterar() {
        String sql = "UPDATE filial SET codigo_filial=?, nome_filial=?, cnpj_filial=? WHERE id_filial=?";
        try {
            pst = conexao.prepareStatement(sql);

            // Definição dos 11 primeiros parâmetros
            pst.setString(1, txtCodigoFilial.getText());
            pst.setString(2, txtNomeFilial.getText());
            pst.setString(3, txtCnpjFilial.getText());
            pst.setString(4, txtIdFilial.getText());

            // Validação de campos obrigatórios
            if (txtCodigoFilial.getText().isEmpty() || txtNomeFilial.getText().isEmpty()
                    || txtCnpjFilial.getText().isEmpty()) {
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
        int confirma = JOptionPane.showConfirmDialog(null, "Você tem certeza que deseja excluir esta Filial do sistema?", "Atenção", JOptionPane.YES_NO_OPTION);
        if (confirma == JOptionPane.YES_OPTION) {
            String sql = "delete from filial where id_filial=?";
            try {
                pst = conexao.prepareStatement(sql);
                pst.setString(1, txtIdFilial.getText());
                int excluido = pst.executeUpdate();
                if (excluido > 0) {
                    JOptionPane.showMessageDialog(null, "Filial excluida com sucesso!");
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
        txtPesquisarFilial.setText(null);
        txtIdFilial.setText(null);
        txtCodigoFilial.setText(null);
        txtNomeFilial.setText(null);
        txtCnpjFilial.setText(null);
        ((DefaultTableModel) tblFilial.getModel()).setRowCount(0);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblFilial = new javax.swing.JTable();
        jLabel3 = new javax.swing.JLabel();
        txtNomeFilial = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        txtCodigoFilial = new javax.swing.JTextField();
        txtPesquisarFilial = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        txtIdFilial = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        txtCnpjFilial = new javax.swing.JFormattedTextField();
        jPanel2 = new javax.swing.JPanel();
        btnCadastrar = new javax.swing.JButton();
        btnAtualizar = new javax.swing.JButton();
        btnExcluir = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setTitle("Cadastrar Filial");
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));

        jTabbedPane1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        tblFilial.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Código", "Código Filial", "Nome", "CNPJ"
            }
        ));
        tblFilial.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblFilialMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblFilial);

        jLabel3.setText("CNPJ");

        jLabel2.setText("Nome do Grupo");

        jLabel1.setText("Código Filial");

        txtCodigoFilial.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtCodigoFilialActionPerformed(evt);
            }
        });

        txtPesquisarFilial.setBackground(new java.awt.Color(255, 255, 204));
        txtPesquisarFilial.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtPesquisarFilialKeyReleased(evt);
            }
        });

        jLabel4.setText("Busca por nome:");

        jLabel5.setText("Código");

        txtIdFilial.setEditable(false);

        jLabel6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/megacenter/icones/pesquisar.png"))); // NOI18N

        try {
            txtCnpjFilial.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("##.###.###/####-##")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(txtPesquisarFilial, javax.swing.GroupLayout.PREFERRED_SIZE, 179, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(4, 4, 4)
                        .addComponent(jLabel6))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addGap(6, 6, 6)
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(6, 6, 6)
                        .addComponent(jLabel2)
                        .addGap(264, 264, 264)
                        .addComponent(jLabel3))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(txtIdFilial, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(6, 6, 6)
                        .addComponent(txtCodigoFilial, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(8, 8, 8)
                        .addComponent(txtNomeFilial, javax.swing.GroupLayout.PREFERRED_SIZE, 332, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(6, 6, 6)
                        .addComponent(txtCnpjFilial, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 605, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(11, 11, 11)
                .addComponent(jLabel4)
                .addGap(6, 6, 6)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtPesquisarFilial, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(1, 1, 1)
                        .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(11, 11, 11)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel5)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3))
                .addGap(4, 4, 4)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtIdFilial, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtCodigoFilial, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtNomeFilial, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtCnpjFilial, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 311, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("Cadastro", jPanel1);

        jPanel2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));

        btnCadastrar.setBackground(new java.awt.Color(255, 255, 204));
        btnCadastrar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/megacenter/icones/adicionar.png"))); // NOI18N
        btnCadastrar.setText("Cadastrar");
        btnCadastrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCadastrarActionPerformed(evt);
            }
        });

        btnAtualizar.setBackground(new java.awt.Color(255, 255, 204));
        btnAtualizar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/megacenter/icones/editar.png"))); // NOI18N
        btnAtualizar.setText("Atualizar");
        btnAtualizar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAtualizarActionPerformed(evt);
            }
        });

        btnExcluir.setBackground(new java.awt.Color(255, 255, 204));
        btnExcluir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/megacenter/icones/excluir.png"))); // NOI18N
        btnExcluir.setText("Excluir");
        btnExcluir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExcluirActionPerformed(evt);
            }
        });

        jButton1.setBackground(new java.awt.Color(255, 255, 204));
        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/megacenter/icones/Sair.png"))); // NOI18N
        jButton1.setText("Sair");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton1)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(btnCadastrar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnAtualizar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnExcluir, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnCadastrar)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnAtualizar)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnExcluir)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton1)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 632, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(11, 11, 11)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jTabbedPane1))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        setBounds(0, 0, 797, 510);
    }// </editor-fold>//GEN-END:initComponents

    private void txtCodigoFilialActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCodigoFilialActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtCodigoFilialActionPerformed

    private void btnCadastrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCadastrarActionPerformed
        // TODO add your handling code here:
        cadastrar();
    }//GEN-LAST:event_btnCadastrarActionPerformed

    private void btnAtualizarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAtualizarActionPerformed
        // TODO add your handling code here:
        alterar();
    }//GEN-LAST:event_btnAtualizarActionPerformed

    private void btnExcluirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExcluirActionPerformed
        // TODO add your handling code here:
        excluir();
    }//GEN-LAST:event_btnExcluirActionPerformed

    private void tblFilialMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblFilialMouseClicked
        // TODO add your handling code here:
        preencher_campos();
    }//GEN-LAST:event_tblFilialMouseClicked

    private void txtPesquisarFilialKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtPesquisarFilialKeyReleased
        // TODO add your handling code here:
        pesquisar_filial();
    }//GEN-LAST:event_txtPesquisarFilialKeyReleased

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // exibi uma caixa de dialogo com a opção sim ou não
        int sair = JOptionPane.showConfirmDialog(null, "Tem certeza que você deseja sair?", "Atenção", JOptionPane.YES_NO_CANCEL_OPTION);
        if (sair == JOptionPane.YES_NO_OPTION) {
            System.exit(0);
        }
    }//GEN-LAST:event_jButton1ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAtualizar;
    private javax.swing.JButton btnCadastrar;
    private javax.swing.JButton btnExcluir;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTable tblFilial;
    private javax.swing.JFormattedTextField txtCnpjFilial;
    private javax.swing.JTextField txtCodigoFilial;
    private javax.swing.JTextField txtIdFilial;
    private javax.swing.JTextField txtNomeFilial;
    private javax.swing.JTextField txtPesquisarFilial;
    // End of variables declaration//GEN-END:variables
}
