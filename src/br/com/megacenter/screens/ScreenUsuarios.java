/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.megacenter.screens;

/**
 *
 * @author JANDERSON
 */
import javax.swing.GroupLayout;
import java.sql.*;
import br.com.megacenter.dal.ModuloConexao;
import com.toedter.calendar.JDateChooser;
import java.awt.Image;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

public class ScreenUsuarios extends javax.swing.JInternalFrame {

    // usando a váriavel da conexão dal
    Connection conexao = null;
    PreparedStatement pst = null;
    ResultSet rs = null;

    // classe construtora
    public ScreenUsuarios() {
        initComponents();
        conexao = ModuloConexao.conector();

        txtCepUsu.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent e) {
                // Pegando somente números
                String cep = txtCepUsu.getText().replaceAll("\\D", "");
                // Só busca com todos os 8 números do CEP
                if (cep.length() == 8) {
                    buscarCep();
                }
            }
        });

        // opcional: focus lost para buscar quando o usuário sair do campo
        txtCepUsu.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                String cep = txtCepUsu.getText().replaceAll("\\D", "");
                if (cep.length() == 8) {
                    buscarCep();
                }
            }
        });
    }

    // a linha abaxio cria o medo consultar 
    private void consultar() {

        String sql = "SELECT * FROM usuarios WHERE idusuario=?";

        try {
            pst = conexao.prepareStatement(sql);
            pst.setString(1, txtIdUsu.getText());
            rs = pst.executeQuery();

            if (rs.next()) {

                txtUsuarioUsu.setText(rs.getString("usuario"));
                txtSenhaUsu.setText(rs.getString("senha"));
                txtNomeUsu.setText(rs.getString("nome"));
                txtEmailUsu.setText(rs.getString("email"));
                txtTelefoneUsu.setText(rs.getString("telefone"));
                txtEnderecoUsu.setText(rs.getString("endereco"));
                txtComplementoUsu.setText(rs.getString("complemento"));
                txtNumeroUsu.setText(rs.getString("numero"));
                txtCepUsu.setText(rs.getString("cep"));
                txtUfUsu.setText(rs.getString("uf"));
                cboUsoPerfil.setSelectedItem(rs.getString("perfil"));
                txtBairroUsu.setText(rs.getString("bairro"));
                txtCpfUsu.setText(rs.getString("cpf"));
                txtMatriculaUsu.setText(rs.getString("matricula"));
                txtDataNascimentoUsu.setText(rs.getString("data_de_nascimento"));
                txtFilialUsu.setText(rs.getString("filial"));
                txtCargoUsu.setText(rs.getString("cargo"));
                txtAreaatuaUsu.setText(rs.getString("area_de_atuacao"));
                txtMotivoInatUsul.setText(rs.getString("motivo"));
                txtCidadeUsu.setText(rs.getString("cidade"));
                txtCelularUsu.setText(rs.getString("celular"));
                TxtNacionalidadeUsu.setText(rs.getString("nacionalidade"));
                TxtEstadoCivilUsu.setText(rs.getString("estado_civil"));
                TxtProfissaoUsu.setText(rs.getString("profissao"));
                txtRgUsu.setText(rs.getString("rg"));
                txtOrgaoEmissorUsu.setText(rs.getString("orgao_emissor"));
                txtCtpsUsu.setText(rs.getString("ctps"));
                txtPisUsu.setText(rs.getString("pis"));
                txtBancoUsu.setText(rs.getString("banco"));
                txtAgenciaUsu.setText(rs.getString("agencia"));
                txtDvagenUsu.setText(rs.getString("dv_agencia"));
                txtContacorrenteUsu.setText(rs.getString("conta_corrente"));
                txtDvcontUsu.setText(rs.getString("dv_conta"));
                cboTipoPixUsul.setSelectedItem(rs.getString("tipo_de_chave_pix"));
                txtChavepixUsu.setText(rs.getString("chave_pix"));

                // ✅ Datas JDateChooser
                java.sql.Date adm = rs.getDate("data_admissao");
                java.sql.Date dem = rs.getDate("data_demissao");

                txtDtadmissaoUsu.setDate(adm != null ? new java.util.Date(adm.getTime()) : null);
                txtDtdemissaoUsu.setDate(dem != null ? new java.util.Date(dem.getTime()) : null);

                // ✅ SITUAÇÃO: ativo / inativo
                String situacao = rs.getString("situacao");
                rbtAtivoUsu.setSelected("ativo".equalsIgnoreCase(situacao));
                rbtInativoUsu.setSelected("inativo".equalsIgnoreCase(situacao));

                // ✅ TIPO FUNÇÃO
                String tipoFuncao = rs.getString("tipo_funcao");
                rbtTipoFuncionarioUsu.setSelected("funcionario".equalsIgnoreCase(tipoFuncao));
                rbtTipoMotoristaUsu.setSelected("motorista".equalsIgnoreCase(tipoFuncao));
                rbtTipoVendedorUsu.setSelected("vendedor".equalsIgnoreCase(tipoFuncao));

            } else {
                JOptionPane.showMessageDialog(null, "Usuário não encontrado.");
                limpar_campos();
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Erro ao consultar: " + e.getMessage());
        }
    }

    // cirando o metodo cadastrar
    private void cadastrar() {

        String sql = "INSERT INTO usuarios ("
                + "usuario, senha, nome, email, telefone, endereco, complemento, numero, cep, uf, perfil, bairro, cpf, matricula, "
                + "data_de_nascimento, filial, cargo, area_de_atuacao, motivo, cidade, celular, nacionalidade, estado_civil, profissao, "
                + "rg, orgao_emissor, ctps, pis, banco, agencia, dv_agencia, conta_corrente, dv_conta, tipo_de_chave_pix, chave_pix, "
                + "tipo_funcao, situacao, data_admissao, data_demissao, foto_usuario"
                + ") VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

        
        // Validação de campos obrigatórios
        if (txtFilialUsu.getText().trim().isEmpty()
                || txtNomeUsu.getText().trim().isEmpty()
                || txtUsuarioUsu.getText().trim().isEmpty()
                || txtSenhaUsu.getText().trim().isEmpty()
                || txtCargoUsu.getText().trim().isEmpty()
                || cboUsoPerfil.getSelectedItem() == null
                || txtDtadmissaoUsu.getDate() == null) {

            JOptionPane.showMessageDialog(null, "Por favor, preencha todos os campos obrigatórios.");
            return;
        }

        // Validação da imagem
        File imagem = new File(txtDiretorioImagemUsuario.getText());
        if (!imagem.exists() || !imagem.isFile()) {
            JOptionPane.showMessageDialog(null, "Imagem não encontrada ou caminho inválido.");
            return;
        }
        
        try {
            pst = conexao.prepareStatement(sql);

            pst.setString(1, txtUsuarioUsu.getText());
            pst.setString(2, txtSenhaUsu.getText());
            pst.setString(3, txtNomeUsu.getText());
            pst.setString(4, txtEmailUsu.getText());
            pst.setString(5, txtTelefoneUsu.getText());
            pst.setString(6, txtEnderecoUsu.getText());
            pst.setString(7, txtComplementoUsu.getText());
            pst.setString(8, txtNumeroUsu.getText());
            pst.setString(9, txtCepUsu.getText());
            pst.setString(10, txtUfUsu.getText());
            pst.setString(11, cboUsoPerfil.getSelectedItem().toString());
            pst.setString(12, txtBairroUsu.getText());
            pst.setString(13, txtCpfUsu.getText());
            pst.setString(14, txtMatriculaUsu.getText());
            pst.setString(15, txtDataNascimentoUsu.getText());
            pst.setString(16, txtFilialUsu.getText());
            pst.setString(17, txtCargoUsu.getText());
            pst.setString(18, txtAreaatuaUsu.getText());
            pst.setString(19, txtMotivoInatUsul.getText());
            pst.setString(20, txtCidadeUsu.getText());
            pst.setString(21, txtCelularUsu.getText());
            pst.setString(22, TxtNacionalidadeUsu.getText());
            pst.setString(23, TxtEstadoCivilUsu.getText());
            pst.setString(24, TxtProfissaoUsu.getText());
            pst.setString(25, txtRgUsu.getText());
            pst.setString(26, txtOrgaoEmissorUsu.getText());
            pst.setString(27, txtCtpsUsu.getText());
            pst.setString(28, txtPisUsu.getText());
            pst.setString(29, txtBancoUsu.getText());
            pst.setString(30, txtAgenciaUsu.getText());
            pst.setString(31, txtDvagenUsu.getText());
            pst.setString(32, txtContacorrenteUsu.getText());
            pst.setString(33, txtDvcontUsu.getText());
            pst.setString(34, cboTipoPixUsul.getSelectedItem().toString());
            pst.setString(35, txtChavepixUsu.getText());

            // RADIO BUTTON: tipo de função
            pst.setString(36,
                    rbtTipoFuncionarioUsu.isSelected() ? "funcionario"
                    : rbtTipoMotoristaUsu.isSelected() ? "motorista"
                    : rbtTipoVendedorUsu.isSelected() ? "vendedor"
                    : ""
            );

            // RADIO BUTTON: situação
            pst.setString(37, rbtAtivoUsu.isSelected() ? "ativo" : "inativo");

            // ADMISSÃO
            java.util.Date adm = txtDtadmissaoUsu.getDate();
            pst.setDate(38, adm != null ? new java.sql.Date(adm.getTime()) : null);

            // DEMISSÃO
            java.util.Date dem = txtDtdemissaoUsu.getDate();
            pst.setDate(39, dem != null ? new java.sql.Date(dem.getTime()) : null);
            
            

            // EXECUTA DEPOIS DE SETAR TUDO
            int cadastrou = pst.executeUpdate();

            if (cadastrou > 0) {
                JOptionPane.showMessageDialog(null, "Usuário cadastrado com sucesso!");
                limpar_campos();
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Erro ao cadastrar usuário: " + e.getMessage());
        }
    }

    // criando o metodo para alterar os dados do usuário no banco de dados
    private void alterar() {

        String sql = "UPDATE usuarios SET "
                + "usuario=?, senha=?, nome=?, email=?, telefone=?, endereco=?, "
                + "complemento=?, numero=?, cep=?, uf=?, perfil=?, bairro=?, cpf=?, matricula=?, "
                + "data_de_nascimento=?, filial=?, cargo=?, area_de_atuacao=?, motivo=?, cidade=?, celular=?, "
                + "nacionalidade=?, estado_civil=?, profissao=?, rg=?, orgao_emissor=?, ctps=?, pis=?, banco=?, "
                + "agencia=?, dv_agencia=?, conta_corrente=?, dv_conta=?, tipo_de_chave_pix=?, chave_pix=?, "
                + "tipo_funcao=?, situacao=?, data_admissao=?, data_demissao=?, foto_usuario=?, "
                + "WHERE idusuario=?";

        try {
            pst = conexao.prepareStatement(sql);

            pst.setString(1, txtUsuarioUsu.getText());
            pst.setString(2, txtSenhaUsu.getText());
            pst.setString(3, txtNomeUsu.getText());
            pst.setString(4, txtEmailUsu.getText());
            pst.setString(5, txtTelefoneUsu.getText());
            pst.setString(6, txtEnderecoUsu.getText());
            pst.setString(7, txtComplementoUsu.getText());
            pst.setString(8, txtNumeroUsu.getText());
            pst.setString(9, txtCepUsu.getText());
            pst.setString(10, txtUfUsu.getText());
            pst.setString(11, cboUsoPerfil.getSelectedItem() == null ? "" : cboUsoPerfil.getSelectedItem().toString());
            pst.setString(12, txtBairroUsu.getText());
            pst.setString(13, txtCpfUsu.getText());
            pst.setString(14, txtMatriculaUsu.getText());
            pst.setString(15, txtDataNascimentoUsu.getText());
            pst.setString(16, txtFilialUsu.getText());
            pst.setString(17, txtCargoUsu.getText());
            pst.setString(18, txtAreaatuaUsu.getText());
            pst.setString(19, txtMotivoInatUsul.getText());
            pst.setString(20, txtCidadeUsu.getText());
            pst.setString(21, txtCelularUsu.getText());
            pst.setString(22, TxtNacionalidadeUsu.getText());
            pst.setString(23, TxtEstadoCivilUsu.getText());
            pst.setString(24, TxtProfissaoUsu.getText());
            pst.setString(25, txtRgUsu.getText());
            pst.setString(26, txtOrgaoEmissorUsu.getText());
            pst.setString(27, txtCtpsUsu.getText());
            pst.setString(28, txtPisUsu.getText());
            pst.setString(29, txtBancoUsu.getText());
            pst.setString(30, txtAgenciaUsu.getText());
            pst.setString(31, txtDvagenUsu.getText());
            pst.setString(32, txtContacorrenteUsu.getText());
            pst.setString(33, txtDvcontUsu.getText());
            pst.setString(34, cboTipoPixUsul.getSelectedItem() == null ? "" : cboTipoPixUsul.getSelectedItem().toString());
            pst.setString(35, txtChavepixUsu.getText());

            // tipo_funcao
            String tipoFuncao = "";
            if (rbtTipoFuncionarioUsu.isSelected()) {
                tipoFuncao = "funcionario";
            } else if (rbtTipoMotoristaUsu.isSelected()) {
                tipoFuncao = "motorista";
            } else if (rbtTipoVendedorUsu.isSelected()) {
                tipoFuncao = "vendedor";
            }
            pst.setString(36, tipoFuncao);

            // situacao
            pst.setString(37, rbtAtivoUsu.isSelected() ? "ativo" : "inativo");

            // datas JDateChooser -> Date SQL (igual ao cadastrar)
            java.util.Date adm = txtDtadmissaoUsu.getDate();
            java.util.Date dem = txtDtdemissaoUsu.getDate();

            pst.setDate(38, adm != null ? new java.sql.Date(adm.getTime()) : null);
            pst.setDate(39, dem != null ? new java.sql.Date(dem.getTime()) : null);

            // ID (posição 40)
            if (txtIdUsu.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Informe o ID para alterar!");
                return;
            }
            pst.setString(40, txtIdUsu.getText());

            int alterou = pst.executeUpdate();

            if (alterou > 0) {
                JOptionPane.showMessageDialog(null, "Dados alterados com sucesso!");
                limpar_campos();
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Erro ao alterar usuário: " + e.getMessage());
        }
    }

    // criando o metodo responsável pela exlusão do usuário no banco de dados
    private void excluir() {
        // a estrutura abaxio confirma a exclusão do usuário
        int confirma = JOptionPane.showConfirmDialog(null, "Você tem certeza que deseja excluir este usuário do sistema?", "Atenção", JOptionPane.YES_NO_OPTION);
        if (confirma == JOptionPane.YES_OPTION) {
            String sql = "delete from usuarios where idusuario=?";
            try {
                pst = conexao.prepareStatement(sql);
                pst.setString(1, txtIdUsu.getText());
                int excluido = pst.executeUpdate();
                if (excluido > 0) {
                    JOptionPane.showMessageDialog(null, "Usuário excluido com sucesso!");
                    // As linhas abaixo limpam todos os campos
                    limpar_campos();
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, e);
            }
        }
    }

    // limpar campos e habilitar os botões e gerenciar os botões
    private void limpar_campos() {
        txtIdUsu.setText(null);
        txtUsuarioUsu.setText(null);
        txtSenhaUsu.setText(null);
        txtNomeUsu.setText(null);
        txtEmailUsu.setText(null);
        txtTelefoneUsu.setText(null);
        txtEnderecoUsu.setText(null);
        txtComplementoUsu.setText(null);
        txtNumeroUsu.setText(null);
        txtCepUsu.setText(null);
        txtUfUsu.setText(null);
        txtBairroUsu.setText(null);
        txtCpfUsu.setText(null);
        txtMatriculaUsu.setText(null);
        txtDataNascimentoUsu.setText(null);
        txtFilialUsu.setText(null);
        txtCargoUsu.setText(null);
        txtAreaatuaUsu.setText(null);
        txtMotivoInatUsul.setText(null);
        txtCidadeUsu.setText(null);
        txtCelularUsu.setText(null);
        TxtNacionalidadeUsu.setText(null);
        TxtEstadoCivilUsu.setText(null);
        TxtProfissaoUsu.setText(null);
        txtRgUsu.setText(null);
        txtOrgaoEmissorUsu.setText(null);
        txtCtpsUsu.setText(null);
        txtPisUsu.setText(null);
        txtBancoUsu.setText(null);
        txtAgenciaUsu.setText(null);
        txtDvagenUsu.setText(null);
        txtContacorrenteUsu.setText(null);
        txtDvcontUsu.setText(null);
        cboTipoPixUsul.setSelectedItem(null);
        txtChavepixUsu.setText(null);
        cboUsoPerfil.setSelectedItem(null);
        txtDiretorioImagemUsuario.setText(null);

        // limpa radio buttons
        rbtAtivoUsu.setSelected(false);
        rbtInativoUsu.setSelected(false);
        rbtTipoFuncionarioUsu.setSelected(false);
        rbtTipoMotoristaUsu.setSelected(false);
        rbtTipoVendedorUsu.setSelected(false);

        // limpando as datas (se forem JDateChooser)
        try {
            txtDtadmissaoUsu.setDate(null);
        } catch (Exception ex) { /* ignora se não for JDateChooser */ }
        try {
            txtDtdemissaoUsu.setDate(null);
        } catch (Exception ex) { /* ignora */ }
    }

    // --------------------------------------------------------------------
    // 🔹 SALVAR (NOVO MÉTODO)
    // --------------------------------------------------------------------
    /**
     * O botão salvar serve quando o usuário já foi consultado e o operador
     * apenas alterou os dados em tela.
     *
     * → Se o ID existir no banco, altera. → Caso contrário, cadastra.
     */
    private void salvar() {

        // Consulta se o ID existe
        String sql = "SELECT idusuario FROM usuarios WHERE idusuario=?";

        try {
            pst = conexao.prepareStatement(sql);
            pst.setString(1, txtIdUsu.getText());
            rs = pst.executeQuery();

            if (rs.next()) {
                alterar();
            } else {
                cadastrar();
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }

    /**
     * Aplica máscara somente se o campo for realmente JFormattedTextField
     */
    private void aplicarMascaraSeguro(JTextField campo,
            java.util.function.Consumer<JFormattedTextField> mascara) {

        if (campo instanceof JFormattedTextField) {
            mascara.accept((JFormattedTextField) campo);
        } else {
            System.err.println("⚠ Campo não é JFormattedTextField: " + campo.getName());
        }
    }

    // Método para buscar informações do CEP via API pública ViaCEP
    private void buscarCep() {

        String cep = txtCepUsu.getText().replaceAll("\\D", "");

        if (cep.length() != 8) {
            JOptionPane.showMessageDialog(null, "CEP inválido.");
            return;
        }

        try {
            java.net.URL url = new java.net.URL("https://viacep.com.br/ws/" + cep + "/json/");

            java.io.BufferedReader br = new java.io.BufferedReader(
                    new java.io.InputStreamReader(url.openStream(), "UTF-8"));

            StringBuilder json = new StringBuilder();
            String linha;

            while ((linha = br.readLine()) != null) {
                json.append(linha);
            }

            br.close();

            org.json.JSONObject obj = new org.json.JSONObject(json.toString());

            if (obj.has("erro")) {
                JOptionPane.showMessageDialog(null, "CEP não encontrado.");
                return;
            }

            txtEnderecoUsu.setText(obj.optString("logradouro", ""));
            txtBairroUsu.setText(obj.optString("bairro", ""));
            txtCidadeUsu.setText(obj.optString("localidade", "")); // ✅ CORRIGIDO
            txtUfUsu.setText(obj.optString("uf", ""));

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Erro ao buscar CEP:\n" + e.getMessage());
        }
    }

    // criando o metodo para gerar a matricula do usuário manualmente através do java
    private String gerarMatriculaAutomatica() {
        String sql = "SELECT IFNULL(MAX(CAST(matricula AS UNSIGNED)), 0) + 1 AS novaMatricula FROM usuarios";

        try {
            pst = conexao.prepareStatement(sql);
            rs = pst.executeQuery();

            if (rs.next()) {
                int numero = rs.getInt("novaMatricula");
                return String.format("%05d", numero); // gera 00001, 00002, 00150...
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Erro ao gerar matrícula: " + e);
        }
        return null;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel13 = new javax.swing.JLabel();
        jTabbedPane2 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        txtNomeUsu = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        txtIdUsu = new javax.swing.JTextField();
        jLabel14 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        txtSenhaUsu = new javax.swing.JPasswordField();
        jLabel4 = new javax.swing.JLabel();
        txtUsuarioUsu = new javax.swing.JTextField();
        cboUsoPerfil = new javax.swing.JComboBox();
        jLabel1 = new javax.swing.JLabel();
        txtMatriculaUsu = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        lblFotoUsuario = new javax.swing.JLabel();
        txtDiretorioImagemUsuario = new javax.swing.JTextField();
        txtFilialUsu = new javax.swing.JTextField();
        jLabel16 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        txtEnderecoUsu = new javax.swing.JTextField();
        txtBairroUsu = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();
        txtUfUsu = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        txtEmailUsu = new javax.swing.JTextField();
        txtNumeroUsu = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        txtComplementoUsu = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        txtCidadeUsu = new javax.swing.JTextField();
        jLabel42 = new javax.swing.JLabel();
        txtTelefoneUsu = new javax.swing.JFormattedTextField();
        txtCelularUsu = new javax.swing.JFormattedTextField();
        txtCepUsu = new javax.swing.JFormattedTextField();
        jPanel3 = new javax.swing.JPanel();
        jLabel20 = new javax.swing.JLabel();
        TxtNacionalidadeUsu = new javax.swing.JTextField();
        jLabel21 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        TxtEstadoCivilUsu = new javax.swing.JTextField();
        jLabel23 = new javax.swing.JLabel();
        TxtProfissaoUsu = new javax.swing.JTextField();
        jLabel17 = new javax.swing.JLabel();
        txtRgUsu = new javax.swing.JTextField();
        jLabel24 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        txtOrgaoEmissorUsu = new javax.swing.JTextField();
        txtCtpsUsu = new javax.swing.JTextField();
        jLabel26 = new javax.swing.JLabel();
        txtPisUsu = new javax.swing.JTextField();
        jLabel27 = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        txtBancoUsu = new javax.swing.JTextField();
        txtAgenciaUsu = new javax.swing.JTextField();
        jLabel29 = new javax.swing.JLabel();
        jLabel30 = new javax.swing.JLabel();
        txtDvagenUsu = new javax.swing.JTextField();
        jLabel31 = new javax.swing.JLabel();
        txtContacorrenteUsu = new javax.swing.JTextField();
        jLabel32 = new javax.swing.JLabel();
        jLabel33 = new javax.swing.JLabel();
        txtDvcontUsu = new javax.swing.JTextField();
        jLabel34 = new javax.swing.JLabel();
        cboTipoPixUsul = new javax.swing.JComboBox();
        jLabel35 = new javax.swing.JLabel();
        txtChavepixUsu = new javax.swing.JTextField();
        jLabel18 = new javax.swing.JLabel();
        txtCpfUsu = new javax.swing.JFormattedTextField();
        txtDataNascimentoUsu = new javax.swing.JFormattedTextField();
        txtMotivoInatUsul = new javax.swing.JTextField();
        jLabel36 = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        rbtAtivoUsu = new javax.swing.JRadioButton();
        rbtInativoUsu = new javax.swing.JRadioButton();
        jPanel6 = new javax.swing.JPanel();
        rbtTipoFuncionarioUsu = new javax.swing.JRadioButton();
        rbtTipoMotoristaUsu = new javax.swing.JRadioButton();
        rbtTipoVendedorUsu = new javax.swing.JRadioButton();
        jLabel37 = new javax.swing.JLabel();
        txtDtadmissaoUsu = new com.toedter.calendar.JDateChooser();
        txtDtdemissaoUsu = new com.toedter.calendar.JDateChooser();
        jLabel38 = new javax.swing.JLabel();
        jLabel39 = new javax.swing.JLabel();
        txtCargoUsu = new javax.swing.JTextField();
        txtAreaatuaUsu = new javax.swing.JTextField();
        jLabel40 = new javax.swing.JLabel();
        jLabel41 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblUsuarios = new javax.swing.JTable();
        jPanel2 = new javax.swing.JPanel();
        btnCadastrarUsu = new javax.swing.JButton();
        btnAtualizarUsu = new javax.swing.JButton();
        btnConsultarUsu = new javax.swing.JButton();
        btnDeletarUsu = new javax.swing.JButton();
        btnInserirImagemUsuario = new javax.swing.JButton();
        btnSalvarUsu = new javax.swing.JButton();

        jLabel13.setText("jLabel13");

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setTitle("528 - Cadastro de Usuário/Setor");
        setPreferredSize(new java.awt.Dimension(630, 480));
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jTabbedPane2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255)));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        txtNomeUsu.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED, null, new java.awt.Color(204, 204, 204), null, null));
        jPanel1.add(txtNomeUsu, new org.netbeans.lib.awtextra.AbsoluteConstraints(267, 25, 274, -1));

        jLabel2.setText("Nome *");
        jPanel1.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(271, 5, 70, -1));

        txtIdUsu.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED, null, new java.awt.Color(204, 204, 204), null, null));
        jPanel1.add(txtIdUsu, new org.netbeans.lib.awtextra.AbsoluteConstraints(11, 25, 55, -1));

        jLabel14.setText("Código");
        jPanel1.add(jLabel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(11, 5, -1, -1));

        jLabel3.setText("Senha: *");
        jPanel1.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(551, 51, 70, -1));

        txtSenhaUsu.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED, null, new java.awt.Color(204, 204, 204), null, null));
        jPanel1.add(txtSenhaUsu, new org.netbeans.lib.awtextra.AbsoluteConstraints(551, 71, 132, -1));

        jLabel4.setText("Usuário (login) *");
        jPanel1.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(551, 5, 91, -1));

        txtUsuarioUsu.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED, null, new java.awt.Color(204, 204, 204), null, null));
        jPanel1.add(txtUsuarioUsu, new org.netbeans.lib.awtextra.AbsoluteConstraints(551, 25, 132, -1));

        cboUsoPerfil.setModel(new javax.swing.DefaultComboBoxModel(new String[] { " ", "admin", "Técnico", "Usuário", "colaborador", " " }));
        jPanel1.add(cboUsoPerfil, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 120, 130, -1));

        jLabel1.setText("Matricula:");
        jPanel1.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(143, 5, -1, -1));

        txtMatriculaUsu.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED, null, new java.awt.Color(204, 204, 204), null, null));
        txtMatriculaUsu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtMatriculaUsuActionPerformed(evt);
            }
        });
        jPanel1.add(txtMatriculaUsu, new org.netbeans.lib.awtextra.AbsoluteConstraints(139, 25, 118, -1));

        jLabel12.setText("Perfil *");
        jPanel1.add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 100, 60, -1));

        lblFotoUsuario.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED, null, new java.awt.Color(204, 204, 204), null, null));
        jPanel1.add(lblFotoUsuario, new org.netbeans.lib.awtextra.AbsoluteConstraints(693, 25, 142, 137));

        txtDiretorioImagemUsuario.setEditable(false);
        txtDiretorioImagemUsuario.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED, null, new java.awt.Color(204, 204, 204), null, null));
        jPanel1.add(txtDiretorioImagemUsuario, new org.netbeans.lib.awtextra.AbsoluteConstraints(693, 169, 140, -1));

        txtFilialUsu.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED, null, new java.awt.Color(204, 204, 204), null, null));
        txtFilialUsu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtFilialUsuActionPerformed(evt);
            }
        });
        jPanel1.add(txtFilialUsu, new org.netbeans.lib.awtextra.AbsoluteConstraints(76, 25, 57, -1));

        jLabel16.setText("Filial *");
        jPanel1.add(jLabel16, new org.netbeans.lib.awtextra.AbsoluteConstraints(76, 5, 57, -1));

        jPanel4.setBackground(new java.awt.Color(255, 255, 255));
        jPanel4.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));

        jLabel7.setText("Endereço");

        txtEnderecoUsu.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED, null, new java.awt.Color(204, 204, 204), null, null));

        txtBairroUsu.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED, null, new java.awt.Color(204, 204, 204), null, null));

        jLabel15.setText("Bairro");

        txtUfUsu.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED, null, new java.awt.Color(204, 204, 204), null, null));
        txtUfUsu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtUfUsuActionPerformed(evt);
            }
        });

        jLabel11.setText("UF");

        jLabel6.setText("Telefone");

        jLabel19.setText("Celular");

        jLabel10.setText("CEP (até 8 números)");

        jLabel5.setText("E-mail");

        txtEmailUsu.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED, null, new java.awt.Color(204, 204, 204), null, null));

        txtNumeroUsu.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED, null, new java.awt.Color(204, 204, 204), null, null));
        txtNumeroUsu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtNumeroUsuActionPerformed(evt);
            }
        });

        jLabel9.setText("Número");

        txtComplementoUsu.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED, null, new java.awt.Color(204, 204, 204), null, null));

        jLabel8.setText("Complemento:");

        jLabel42.setText("Cidade");

        try {
            txtTelefoneUsu.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("(##) ####-####")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }

        try {
            txtCelularUsu.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("(##) 9####-####")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }

        try {
            txtCepUsu.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("#####-###")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(124, 124, 124)
                        .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(txtEnderecoUsu, javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel4Layout.createSequentialGroup()
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, 108, Short.MAX_VALUE)
                                    .addComponent(txtTelefoneUsu))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jLabel19, javax.swing.GroupLayout.DEFAULT_SIZE, 108, Short.MAX_VALUE)
                                    .addComponent(txtCelularUsu))))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addComponent(txtNumeroUsu, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtComplementoUsu, javax.swing.GroupLayout.DEFAULT_SIZE, 83, Short.MAX_VALUE)))
                            .addComponent(txtCepUsu))))
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, 142, Short.MAX_VALUE)
                                    .addComponent(txtBairroUsu)))
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addGap(12, 12, 12)
                                .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addComponent(jLabel42)
                                .addGap(0, 183, Short.MAX_VALUE))
                            .addComponent(txtCidadeUsu))
                        .addGap(10, 10, 10)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtUfUsu, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtEmailUsu)))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(jLabel15)
                    .addComponent(jLabel11)
                    .addComponent(jLabel9)
                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel42))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtEnderecoUsu, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtBairroUsu, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtUfUsu, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtNumeroUsu, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtComplementoUsu, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtCidadeUsu, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(jLabel19)
                    .addComponent(jLabel10)
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtEmailUsu, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtTelefoneUsu, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtCelularUsu, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtCepUsu, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel1.add(jPanel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 210, 830, 120));

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));
        jPanel3.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));

        jLabel20.setText("Dt. nascimento");

        TxtNacionalidadeUsu.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED, null, new java.awt.Color(204, 204, 204), null, null));

        jLabel21.setText("Nacionalidade");

        jLabel22.setText("Estado civil");

        TxtEstadoCivilUsu.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED, null, new java.awt.Color(204, 204, 204), null, null));

        jLabel23.setText("Profissão");

        TxtProfissaoUsu.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED, null, new java.awt.Color(204, 204, 204), null, null));

        jLabel17.setText("CPF");

        txtRgUsu.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED, null, new java.awt.Color(204, 204, 204), null, null));

        jLabel24.setText("RG");

        jLabel25.setText("Orgão emissor");

        txtOrgaoEmissorUsu.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED, null, new java.awt.Color(204, 204, 204), null, null));

        txtCtpsUsu.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED, null, new java.awt.Color(204, 204, 204), null, null));

        jLabel26.setText("CTPS");

        txtPisUsu.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED, null, new java.awt.Color(204, 204, 204), null, null));

        jLabel27.setText("PIS");

        jLabel28.setText("Banco");

        txtBancoUsu.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED, null, new java.awt.Color(204, 204, 204), null, null));

        txtAgenciaUsu.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED, null, new java.awt.Color(204, 204, 204), null, null));

        jLabel29.setText("Agência");

        jLabel30.setText("-");

        txtDvagenUsu.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED, null, new java.awt.Color(204, 204, 204), null, null));

        jLabel31.setText("DV");

        txtContacorrenteUsu.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED, null, new java.awt.Color(204, 204, 204), null, null));

        jLabel32.setText("Conta corrente");

        jLabel33.setText("-");

        txtDvcontUsu.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED, null, new java.awt.Color(204, 204, 204), null, null));

        jLabel34.setText("DV");

        cboTipoPixUsul.setBackground(new java.awt.Color(0, 204, 255));
        cboTipoPixUsul.setModel(new javax.swing.DefaultComboBoxModel(new String[] { " ", "01 - Telefone", "02 - E-mail", "03 - CPF/CNPJ", "04 - Chave Aleatória" }));
        cboTipoPixUsul.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED, null, new java.awt.Color(204, 204, 204), null, null));

        jLabel35.setText("Tipo de Chave PIX");

        txtChavepixUsu.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED, null, new java.awt.Color(204, 204, 204), null, null));

        jLabel18.setText("Chave PIX");

        txtCpfUsu.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED, null, new java.awt.Color(204, 204, 204), null, null));
        try {
            txtCpfUsu.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("###.###.###-##")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }

        try {
            txtDataNascimentoUsu.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("##/##/####")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jLabel28, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtBancoUsu, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel20, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 108, Short.MAX_VALUE)
                    .addComponent(jLabel17, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtCpfUsu)
                    .addComponent(txtDataNascimentoUsu))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(TxtNacionalidadeUsu, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel21, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtRgUsu, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel24, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jLabel29, javax.swing.GroupLayout.DEFAULT_SIZE, 60, Short.MAX_VALUE)
                            .addComponent(txtAgenciaUsu))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel30)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel31, javax.swing.GroupLayout.DEFAULT_SIZE, 36, Short.MAX_VALUE)
                            .addComponent(txtDvagenUsu))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel22)
                        .addGap(98, 98, 98)
                        .addComponent(jLabel23)
                        .addGap(377, 377, 377))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(txtOrgaoEmissorUsu, javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel25, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 139, Short.MAX_VALUE)
                                    .addComponent(TxtEstadoCivilUsu, javax.swing.GroupLayout.Alignment.LEADING))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel3Layout.createSequentialGroup()
                                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(txtCtpsUsu, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel26, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel27, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(txtPisUsu, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                    .addComponent(TxtProfissaoUsu, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jLabel32, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                                        .addComponent(txtContacorrenteUsu)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel33)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel3Layout.createSequentialGroup()
                                        .addComponent(jLabel34, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(jLabel35, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(18, 18, 18)
                                        .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(0, 0, Short.MAX_VALUE))
                                    .addGroup(jPanel3Layout.createSequentialGroup()
                                        .addComponent(txtDvcontUsu, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(cboTipoPixUsul, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(txtChavepixUsu)))))
                        .addContainerGap())))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel20)
                    .addComponent(jLabel21)
                    .addComponent(jLabel22)
                    .addComponent(jLabel23))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(TxtNacionalidadeUsu, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(TxtEstadoCivilUsu, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(TxtProfissaoUsu, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtDataNascimentoUsu, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel17)
                    .addComponent(jLabel24)
                    .addComponent(jLabel25)
                    .addComponent(jLabel26)
                    .addComponent(jLabel27))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtRgUsu, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtOrgaoEmissorUsu, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtCtpsUsu, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtPisUsu, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtCpfUsu, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel28)
                    .addComponent(jLabel29)
                    .addComponent(jLabel31)
                    .addComponent(jLabel32)
                    .addComponent(jLabel34)
                    .addComponent(jLabel35)
                    .addComponent(jLabel18))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtBancoUsu, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtAgenciaUsu, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel30)
                        .addComponent(txtDvagenUsu, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtContacorrenteUsu, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel33)
                        .addComponent(txtDvcontUsu, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtChavepixUsu, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(cboTipoPixUsul, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel1.add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 340, 830, 170));

        txtMotivoInatUsul.setBackground(new java.awt.Color(204, 204, 204));
        txtMotivoInatUsul.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED, null, new java.awt.Color(204, 204, 204), null, null));
        jPanel1.add(txtMotivoInatUsul, new org.netbeans.lib.awtextra.AbsoluteConstraints(293, 169, 390, -1));

        jLabel36.setText("Motivo da Inativação");
        jPanel1.add(jLabel36, new org.netbeans.lib.awtextra.AbsoluteConstraints(293, 148, 390, -1));

        jPanel5.setBackground(new java.awt.Color(255, 255, 255));
        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder("Situação"));

        rbtAtivoUsu.setBackground(new java.awt.Color(255, 255, 255));
        rbtAtivoUsu.setText("Ativo");

        rbtInativoUsu.setBackground(new java.awt.Color(255, 255, 255));
        rbtInativoUsu.setText("Inativo");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(rbtAtivoUsu)
                    .addComponent(rbtInativoUsu))
                .addContainerGap(43, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(rbtAtivoUsu)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(rbtInativoUsu)
                .addContainerGap(13, Short.MAX_VALUE))
        );

        jPanel1.add(jPanel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(11, 97, -1, -1));

        jPanel6.setBackground(new java.awt.Color(255, 255, 255));
        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder("Tipo / Função *"));

        rbtTipoFuncionarioUsu.setBackground(new java.awt.Color(255, 255, 255));
        rbtTipoFuncionarioUsu.setText("Funcionário");

        rbtTipoMotoristaUsu.setBackground(new java.awt.Color(255, 255, 255));
        rbtTipoMotoristaUsu.setText("Motorista");

        rbtTipoVendedorUsu.setBackground(new java.awt.Color(255, 255, 255));
        rbtTipoVendedorUsu.setText("Vendedor");

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(rbtTipoFuncionarioUsu)
                    .addComponent(rbtTipoMotoristaUsu)
                    .addComponent(rbtTipoVendedorUsu))
                .addContainerGap(21, Short.MAX_VALUE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addComponent(rbtTipoFuncionarioUsu)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(rbtTipoMotoristaUsu)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(rbtTipoVendedorUsu)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        jPanel1.add(jPanel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(141, 97, -1, -1));

        jLabel37.setText("Dt. Admissão *");
        jPanel1.add(jLabel37, new org.netbeans.lib.awtextra.AbsoluteConstraints(11, 51, 100, -1));
        jPanel1.add(txtDtadmissaoUsu, new org.netbeans.lib.awtextra.AbsoluteConstraints(11, 71, 120, -1));
        jPanel1.add(txtDtdemissaoUsu, new org.netbeans.lib.awtextra.AbsoluteConstraints(141, 71, 120, -1));

        jLabel38.setText("Dt. Demissão");
        jPanel1.add(jLabel38, new org.netbeans.lib.awtextra.AbsoluteConstraints(141, 51, -1, -1));

        jLabel39.setText("Cargo *");
        jPanel1.add(jLabel39, new org.netbeans.lib.awtextra.AbsoluteConstraints(271, 51, 70, -1));

        txtCargoUsu.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED, null, new java.awt.Color(204, 204, 204), null, null));
        jPanel1.add(txtCargoUsu, new org.netbeans.lib.awtextra.AbsoluteConstraints(271, 71, 130, -1));

        txtAreaatuaUsu.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED, null, new java.awt.Color(204, 204, 204), null, null));
        jPanel1.add(txtAreaatuaUsu, new org.netbeans.lib.awtextra.AbsoluteConstraints(411, 71, 130, -1));

        jLabel40.setText("Área de Atuação");
        jPanel1.add(jLabel40, new org.netbeans.lib.awtextra.AbsoluteConstraints(411, 51, -1, -1));

        jLabel41.setText("Imagem");
        jPanel1.add(jLabel41, new org.netbeans.lib.awtextra.AbsoluteConstraints(693, 5, 107, -1));

        jTabbedPane2.addTab("Dados pessoais", jPanel1);

        tblUsuarios.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Código", "Nome", "Matricula", "E-mail", "Usuário", "Senha"
            }
        ));
        jScrollPane2.setViewportView(tblUsuarios);

        jTabbedPane2.addTab("Consulta de Usuários", jScrollPane2);

        getContentPane().add(jTabbedPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 850, 544));

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));

        btnCadastrarUsu.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/megacenter/icones/adicionar.png"))); // NOI18N
        btnCadastrarUsu.setText("Cadastrar");
        btnCadastrarUsu.setToolTipText("Cadastrar");
        btnCadastrarUsu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCadastrarUsuActionPerformed(evt);
            }
        });

        btnAtualizarUsu.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/megacenter/icones/editar.png"))); // NOI18N
        btnAtualizarUsu.setText("Editar");
        btnAtualizarUsu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAtualizarUsuActionPerformed(evt);
            }
        });

        btnConsultarUsu.setText("Consultar");
        btnConsultarUsu.setPreferredSize(new java.awt.Dimension(81, 25));
        btnConsultarUsu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnConsultarUsuActionPerformed(evt);
            }
        });

        btnDeletarUsu.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/megacenter/icones/excluir.png"))); // NOI18N
        btnDeletarUsu.setText("Excluir");
        btnDeletarUsu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeletarUsuActionPerformed(evt);
            }
        });

        btnInserirImagemUsuario.setIcon(new javax.swing.ImageIcon(getClass().getResource("/br/com/megacenter/icones/imagem.png"))); // NOI18N
        btnInserirImagemUsuario.setText("Imagem");
        btnInserirImagemUsuario.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnInserirImagemUsuarioActionPerformed(evt);
            }
        });

        btnSalvarUsu.setText("Salvar");
        btnSalvarUsu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSalvarUsuActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(187, Short.MAX_VALUE)
                .addComponent(btnSalvarUsu, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnInserirImagemUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnCadastrarUsu)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnAtualizarUsu, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnConsultarUsu, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnDeletarUsu, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE, false)
                            .addComponent(btnAtualizarUsu, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnConsultarUsu, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnDeletarUsu)
                            .addComponent(btnCadastrarUsu, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnInserirImagemUsuario)))
                    .addComponent(btnSalvarUsu, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        getContentPane().add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 550, 844, -1));

        setBounds(0, 0, 867, 630);
    }// </editor-fold>//GEN-END:initComponents

    private void txtMatriculaUsuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtMatriculaUsuActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtMatriculaUsuActionPerformed

    private void btnAtualizarUsuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAtualizarUsuActionPerformed
        // Chamando o metodo alterar
        alterar();
    }//GEN-LAST:event_btnAtualizarUsuActionPerformed

    private void btnConsultarUsuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnConsultarUsuActionPerformed
        // Chamando o metodo consultar
        consultar();
    }//GEN-LAST:event_btnConsultarUsuActionPerformed

    private void btnCadastrarUsuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCadastrarUsuActionPerformed
        // Chamando o metodo cadastrar
        cadastrar();
    }//GEN-LAST:event_btnCadastrarUsuActionPerformed

    private void btnDeletarUsuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeletarUsuActionPerformed
        // chamando o método excluir
        excluir();
    }//GEN-LAST:event_btnDeletarUsuActionPerformed

    private void txtFilialUsuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtFilialUsuActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtFilialUsuActionPerformed

    private void txtUfUsuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtUfUsuActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtUfUsuActionPerformed

    private void btnSalvarUsuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSalvarUsuActionPerformed
        salvar();
    }//GEN-LAST:event_btnSalvarUsuActionPerformed

    private void txtNumeroUsuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtNumeroUsuActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtNumeroUsuActionPerformed

    private void btnInserirImagemUsuarioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnInserirImagemUsuarioActionPerformed
        // Criando o metodo para adicionar imagem
        JFileChooser arquivoimagem = new JFileChooser();
        arquivoimagem.setDialogTitle("Selecione uma Imagem");
        arquivoimagem.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int op = arquivoimagem.showOpenDialog(this);
        if (op == JFileChooser.APPROVE_OPTION) {
            File file = new File("");
            file = arquivoimagem.getSelectedFile();
            String fileCodigo = file.getAbsolutePath();
            txtDiretorioImagemUsuario.setText(fileCodigo);
            ImageIcon imagem = new ImageIcon(file.getPath());
            lblFotoUsuario.setIcon(new ImageIcon(imagem.getImage().getScaledInstance(lblFotoUsuario.getWidth(), lblFotoUsuario.getHeight(), Image.SCALE_DEFAULT)));
        }
    }//GEN-LAST:event_btnInserirImagemUsuarioActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField TxtEstadoCivilUsu;
    private javax.swing.JTextField TxtNacionalidadeUsu;
    private javax.swing.JTextField TxtProfissaoUsu;
    private javax.swing.JButton btnAtualizarUsu;
    private javax.swing.JButton btnCadastrarUsu;
    private javax.swing.JButton btnConsultarUsu;
    private javax.swing.JButton btnDeletarUsu;
    private javax.swing.JButton btnInserirImagemUsuario;
    private javax.swing.JButton btnSalvarUsu;
    private javax.swing.JComboBox cboTipoPixUsul;
    private javax.swing.JComboBox cboUsoPerfil;
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
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel40;
    private javax.swing.JLabel jLabel41;
    private javax.swing.JLabel jLabel42;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTabbedPane jTabbedPane2;
    private javax.swing.JLabel lblFotoUsuario;
    private javax.swing.JRadioButton rbtAtivoUsu;
    private javax.swing.JRadioButton rbtInativoUsu;
    private javax.swing.JRadioButton rbtTipoFuncionarioUsu;
    private javax.swing.JRadioButton rbtTipoMotoristaUsu;
    private javax.swing.JRadioButton rbtTipoVendedorUsu;
    private javax.swing.JTable tblUsuarios;
    private javax.swing.JTextField txtAgenciaUsu;
    private javax.swing.JTextField txtAreaatuaUsu;
    private javax.swing.JTextField txtBairroUsu;
    private javax.swing.JTextField txtBancoUsu;
    private javax.swing.JTextField txtCargoUsu;
    private javax.swing.JFormattedTextField txtCelularUsu;
    private javax.swing.JFormattedTextField txtCepUsu;
    private javax.swing.JTextField txtChavepixUsu;
    private javax.swing.JTextField txtCidadeUsu;
    private javax.swing.JTextField txtComplementoUsu;
    private javax.swing.JTextField txtContacorrenteUsu;
    private javax.swing.JFormattedTextField txtCpfUsu;
    private javax.swing.JTextField txtCtpsUsu;
    private javax.swing.JFormattedTextField txtDataNascimentoUsu;
    private javax.swing.JTextField txtDiretorioImagemUsuario;
    private com.toedter.calendar.JDateChooser txtDtadmissaoUsu;
    private com.toedter.calendar.JDateChooser txtDtdemissaoUsu;
    private javax.swing.JTextField txtDvagenUsu;
    private javax.swing.JTextField txtDvcontUsu;
    private javax.swing.JTextField txtEmailUsu;
    private javax.swing.JTextField txtEnderecoUsu;
    private javax.swing.JTextField txtFilialUsu;
    private javax.swing.JTextField txtIdUsu;
    private javax.swing.JTextField txtMatriculaUsu;
    private javax.swing.JTextField txtMotivoInatUsul;
    private javax.swing.JTextField txtNomeUsu;
    private javax.swing.JTextField txtNumeroUsu;
    private javax.swing.JTextField txtOrgaoEmissorUsu;
    private javax.swing.JTextField txtPisUsu;
    private javax.swing.JTextField txtRgUsu;
    private javax.swing.JPasswordField txtSenhaUsu;
    private javax.swing.JFormattedTextField txtTelefoneUsu;
    private javax.swing.JTextField txtUfUsu;
    private javax.swing.JTextField txtUsuarioUsu;
    // End of variables declaration//GEN-END:variables

    private String formatDateForDbFromJDateChooser(JDateChooser txtDtadmissaoUsu) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
