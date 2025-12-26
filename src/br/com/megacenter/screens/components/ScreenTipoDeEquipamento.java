/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.megacenter.screens.components;

import br.com.megacenter.dal.ModuloConexao;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import net.proteanit.sql.DbUtils;

/**
 *
 * @author andin
 */
public class ScreenTipoDeEquipamento extends javax.swing.JInternalFrame {
    
    Connection conexao = null;
    PreparedStatement pst = null;
    ResultSet rs = null;

    /**
     * Creates new form ScreenTipoDeEquipamento
     */
    public ScreenTipoDeEquipamento() {
        initComponents();
        conexao = ModuloConexao.conector();
    }
    
    // cirando o metodo cadastrar do tipo
    private void cadastrar() {
        String sql = "insert into tipo (codigo_tipo, descricao ) values(?,?)";
        try {
            pst = conexao.prepareStatement(sql);
            pst.setString(1, txtCodigoTipo.getText());
            pst.setString(2, txtDescricaoTipo.getText());
            
            // criando a validação dos campos obrigatórios
            if ((txtDescricaoTipo.getText().isEmpty())) {
                JOptionPane.showMessageDialog(null, "Por favor, preencha todos os campos obrigatórios!");
            } else {
                // a linha abaixo atualiza a tabela usuarios com os dados do formulário
                // a estrutura abaixo é uasa para confirmar a alteração dos dados na tabela
                int cadastrado = pst.executeUpdate();
                // a linha abaixo serve de apoio a lógica
                //System.out.println(cadastrado);
                if (cadastrado > 0) {
                    JOptionPane.showMessageDialog(null, "O tipo de equipamento foi cadastrado com sucesso!");
                    limpar();
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }

    // criando o metodo de pesquisa avançada utilizando o nome dos tipo de equipamento no banco de dados
    private void pesquisar_tipo() {
        String sql = " select codigo_tipo as 'Código', descricao as 'Descrição' from tipo where descricao like ?";
        try {
            pst = conexao.prepareStatement(sql);
            // a linha seguir, passa o conteudo da caixa de pesquisa para o ?
            // atenção ao "%" - continuação da String sql
            pst.setString(1, txtPesquisaTipo.getText() + "%");
            rs = pst.executeQuery();
            // a linha abaixo usa a biblioteca rs2xml.jar para preencher a tabela
            tblTipoDeEquipamento.setModel(DbUtils.resultSetToTableModel(rs));

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }

    // criando o metodo para preencher os campos do formulário com o conteúdo da tabela
    public void preencher_campos() {
        int preencher = tblTipoDeEquipamento.getSelectedRow();
        txtCodigoTipo.setText(tblTipoDeEquipamento.getModel().getValueAt(preencher, 0).toString());
        txtDescricaoTipo.setText(tblTipoDeEquipamento.getModel().getValueAt(preencher, 1).toString());
        // a linha abaixo vai desativar o botão de cadastrar
        btnCadastrarTipo.setEnabled(false);
    }

    // criando o metodo para altar os dados da empresa
    // criando o metodo para alterar os dados do usuário no banco de dados
    private void alterar() {
        String sql = "UPDATE tipo SET descricao=? WHERE codigo_tipo=?";
        try {
            pst = conexao.prepareStatement(sql);

            // Definição dos 11 primeiros parâmetros
            pst.setString(1, txtDescricaoTipo.getText());
            pst.setString(2, txtCodigoTipo.getText());

            // Validação de campos obrigatórios
            if (txtDescricaoTipo.getText().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Por favor, preencha todos os campos obrigatórios!");
            } else {
                int atualizado = pst.executeUpdate();
                if (atualizado > 0) {
                    JOptionPane.showMessageDialog(null, "Tipo de equipamento atualizado com sucesso!");
                    // chamando o metodo limpar
                    limpar();

                    // a linha abaixo vai reabilitar o botão cadastrar após alterar o dados de uma empresa
                    btnCadastrarTipo.setEnabled(true);
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Erro ao atualizar o tipo de equipamento: " + e.getMessage());
        }
    }

    // criando o metodo responsável pela exlusão de uma empresa no banco de dados
    private void excluir() {
        // a estrutura abaxio confirma a exclusão do usuário
        int confirma = JOptionPane.showConfirmDialog(null, "Você tem certeza que deseja excluir este tipo de equipamento do sistema?", "Atenção", JOptionPane.YES_NO_OPTION);
        if (confirma == JOptionPane.YES_OPTION) {
            String sql = "delete from tipo where codigo_tipo=?";
            try {
                pst = conexao.prepareStatement(sql);
                pst.setString(1, txtCodigoTipo.getText());
                int excluido = pst.executeUpdate();
                if (excluido > 0) {
                    JOptionPane.showMessageDialog(null, "Tipo de equipamento excluido com sucesso!");
                    // chamando o metodo limpar
                    limpar();

                    // a linha abaixo vai reabilitar o botão cadastrar após alterar o dados de uma empresa
                    btnCadastrarTipo.setEnabled(true);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, e);
            }
        }
    }

    // metodo para limpar os campos do formulário e a tabela
    private void limpar() {
        txtPesquisaTipo.setText(null);
        txtCodigoTipo.setText(null);
        txtDescricaoTipo.setText(null);
        ((DefaultTableModel) tblTipoDeEquipamento.getModel()).setRowCount(0);
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
        btnCadastrarTipo = new javax.swing.JButton();
        btnAtualizarTipo = new javax.swing.JButton();
        btnExcluirTipo = new javax.swing.JButton();
        btnSair = new javax.swing.JButton();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txtCodigoTipo = new javax.swing.JTextField();
        txtDescricaoTipo = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblTipoDeEquipamento = new javax.swing.JTable();
        jLabel3 = new javax.swing.JLabel();
        txtPesquisaTipo = new javax.swing.JTextField();

        setBackground(new java.awt.Color(255, 255, 255));
        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setPreferredSize(new java.awt.Dimension(590, 510));

        jPanel1.setBackground(new java.awt.Color(255, 255, 204));
        jPanel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(153, 153, 153)));

        btnCadastrarTipo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/megacenter/icones/adicionar.png"))); // NOI18N
        btnCadastrarTipo.setText("Cadastrar");
        btnCadastrarTipo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCadastrarTipoActionPerformed(evt);
            }
        });

        btnAtualizarTipo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/megacenter/icones/editar.png"))); // NOI18N
        btnAtualizarTipo.setText("Atualizar");
        btnAtualizarTipo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAtualizarTipoActionPerformed(evt);
            }
        });

        btnExcluirTipo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/megacenter/icones/excluir.png"))); // NOI18N
        btnExcluirTipo.setText("Excluir");
        btnExcluirTipo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExcluirTipoActionPerformed(evt);
            }
        });

        btnSair.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/megacenter/icones/Sair.png"))); // NOI18N
        btnSair.setText("Sair");
        btnSair.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSairActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnCadastrarTipo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnAtualizarTipo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnExcluirTipo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnSair, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addComponent(btnCadastrarTipo)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnAtualizarTipo)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnExcluirTipo)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnSair)
                .addContainerGap())
        );

        jTabbedPane1.setBackground(new java.awt.Color(255, 255, 255));

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));

        jLabel1.setText("Código");

        jLabel2.setText("Descrição");

        tblTipoDeEquipamento.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null}
            },
            new String [] {
                "Código", "Descrição"
            }
        ));
        tblTipoDeEquipamento.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblTipoDeEquipamentoMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblTipoDeEquipamento);

        jLabel3.setText("Tipo de equipamento");

        txtPesquisaTipo.setBackground(new java.awt.Color(255, 255, 204));
        txtPesquisaTipo.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtPesquisaTipoKeyReleased(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtPesquisaTipo)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(txtCodigoTipo, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtDescricaoTipo))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel2)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtPesquisaTipo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtCodigoTipo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtDescricaoTipo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 288, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("Cadastro", jPanel2);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTabbedPane1)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jTabbedPane1))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnCadastrarTipoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCadastrarTipoActionPerformed
        // TODO add your handling code here:
        cadastrar();
    }//GEN-LAST:event_btnCadastrarTipoActionPerformed

    private void btnAtualizarTipoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAtualizarTipoActionPerformed
        // TODO add your handling code here:
        alterar();
    }//GEN-LAST:event_btnAtualizarTipoActionPerformed

    private void btnExcluirTipoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExcluirTipoActionPerformed
        // TODO add your handling code here:
        excluir();
    }//GEN-LAST:event_btnExcluirTipoActionPerformed

    private void btnSairActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSairActionPerformed
        // TODO add your handling code here:
        int sair = JOptionPane.showConfirmDialog(null, "Tem certeza que você deseja sair?", "Atenção", JOptionPane.YES_NO_CANCEL_OPTION);
        if (sair == JOptionPane.YES_NO_OPTION) {
            this.dispose();
        }
    }//GEN-LAST:event_btnSairActionPerformed

    private void txtPesquisaTipoKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtPesquisaTipoKeyReleased
        // TODO add your handling code here:
        pesquisar_tipo();
    }//GEN-LAST:event_txtPesquisaTipoKeyReleased

    private void tblTipoDeEquipamentoMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblTipoDeEquipamentoMouseClicked
        // TODO add your handling code here:
        preencher_campos();
    }//GEN-LAST:event_tblTipoDeEquipamentoMouseClicked


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAtualizarTipo;
    private javax.swing.JButton btnCadastrarTipo;
    private javax.swing.JButton btnExcluirTipo;
    private javax.swing.JButton btnSair;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTable tblTipoDeEquipamento;
    private javax.swing.JTextField txtCodigoTipo;
    private javax.swing.JTextField txtDescricaoTipo;
    private javax.swing.JTextField txtPesquisaTipo;
    // End of variables declaration//GEN-END:variables
}
