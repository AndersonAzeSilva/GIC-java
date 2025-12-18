/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.megacenter.controller;

import javax.swing.GroupLayout;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 *
 * @author JANDERSON
 */
@Entity
@Table(name = "empresas", catalog = "dbmegacenter", schema = "")
@NamedQueries({
    @NamedQuery(name = "Empresas.findAll", query = "SELECT e FROM Empresas e"),
    @NamedQuery(name = "Empresas.findByIdempre", query = "SELECT e FROM Empresas e WHERE e.idempre = :idempre"),
    @NamedQuery(name = "Empresas.findByRazaoSocial", query = "SELECT e FROM Empresas e WHERE e.razaoSocial = :razaoSocial"),
    @NamedQuery(name = "Empresas.findByCnpj", query = "SELECT e FROM Empresas e WHERE e.cnpj = :cnpj"),
    @NamedQuery(name = "Empresas.findByEmail", query = "SELECT e FROM Empresas e WHERE e.email = :email"),
    @NamedQuery(name = "Empresas.findByTelefone", query = "SELECT e FROM Empresas e WHERE e.telefone = :telefone"),
    @NamedQuery(name = "Empresas.findByEndereco", query = "SELECT e FROM Empresas e WHERE e.endereco = :endereco"),
    @NamedQuery(name = "Empresas.findByComplemento", query = "SELECT e FROM Empresas e WHERE e.complemento = :complemento"),
    @NamedQuery(name = "Empresas.findByNumero", query = "SELECT e FROM Empresas e WHERE e.numero = :numero"),
    @NamedQuery(name = "Empresas.findByBairro", query = "SELECT e FROM Empresas e WHERE e.bairro = :bairro"),
    @NamedQuery(name = "Empresas.findByCidade", query = "SELECT e FROM Empresas e WHERE e.cidade = :cidade"),
    @NamedQuery(name = "Empresas.findByCep", query = "SELECT e FROM Empresas e WHERE e.cep = :cep"),
    @NamedQuery(name = "Empresas.findByUf", query = "SELECT e FROM Empresas e WHERE e.uf = :uf")})
public class Empresas implements Serializable {
    @Transient
    private PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "idempre")
    private Integer idempre;
    @Basic(optional = false)
    @Column(name = "razao_social")
    private String razaoSocial;
    @Basic(optional = false)
    @Column(name = "cnpj")
    private String cnpj;
    @Basic(optional = false)
    @Column(name = "email")
    private String email;
    @Basic(optional = false)
    @Column(name = "telefone")
    private String telefone;
    @Column(name = "endereco")
    private String endereco;
    @Column(name = "complemento")
    private String complemento;
    @Column(name = "numero")
    private Integer numero;
    @Column(name = "bairro")
    private String bairro;
    @Column(name = "cidade")
    private String cidade;
    @Column(name = "cep")
    private String cep;
    @Column(name = "uf")
    private String uf;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "idempre")
    private List<OrdemDeServicos> ordemDeServicosList;

    public Empresas() {
    }

    public Empresas(Integer idempre) {
        this.idempre = idempre;
    }

    public Empresas(Integer idempre, String razaoSocial, String cnpj, String email, String telefone) {
        this.idempre = idempre;
        this.razaoSocial = razaoSocial;
        this.cnpj = cnpj;
        this.email = email;
        this.telefone = telefone;
    }

    public Integer getIdempre() {
        return idempre;
    }

    public void setIdempre(Integer idempre) {
        Integer oldIdempre = this.idempre;
        this.idempre = idempre;
        changeSupport.firePropertyChange("idempre", oldIdempre, idempre);
    }

    public String getRazaoSocial() {
        return razaoSocial;
    }

    public void setRazaoSocial(String razaoSocial) {
        String oldRazaoSocial = this.razaoSocial;
        this.razaoSocial = razaoSocial;
        changeSupport.firePropertyChange("razaoSocial", oldRazaoSocial, razaoSocial);
    }

    public String getCnpj() {
        return cnpj;
    }

    public void setCnpj(String cnpj) {
        String oldCnpj = this.cnpj;
        this.cnpj = cnpj;
        changeSupport.firePropertyChange("cnpj", oldCnpj, cnpj);
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        String oldEmail = this.email;
        this.email = email;
        changeSupport.firePropertyChange("email", oldEmail, email);
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        String oldTelefone = this.telefone;
        this.telefone = telefone;
        changeSupport.firePropertyChange("telefone", oldTelefone, telefone);
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        String oldEndereco = this.endereco;
        this.endereco = endereco;
        changeSupport.firePropertyChange("endereco", oldEndereco, endereco);
    }

    public String getComplemento() {
        return complemento;
    }

    public void setComplemento(String complemento) {
        String oldComplemento = this.complemento;
        this.complemento = complemento;
        changeSupport.firePropertyChange("complemento", oldComplemento, complemento);
    }

    public Integer getNumero() {
        return numero;
    }

    public void setNumero(Integer numero) {
        Integer oldNumero = this.numero;
        this.numero = numero;
        changeSupport.firePropertyChange("numero", oldNumero, numero);
    }

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        String oldBairro = this.bairro;
        this.bairro = bairro;
        changeSupport.firePropertyChange("bairro", oldBairro, bairro);
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        String oldCidade = this.cidade;
        this.cidade = cidade;
        changeSupport.firePropertyChange("cidade", oldCidade, cidade);
    }

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        String oldCep = this.cep;
        this.cep = cep;
        changeSupport.firePropertyChange("cep", oldCep, cep);
    }

    public String getUf() {
        return uf;
    }

    public void setUf(String uf) {
        String oldUf = this.uf;
        this.uf = uf;
        changeSupport.firePropertyChange("uf", oldUf, uf);
    }

    public List<OrdemDeServicos> getOrdemDeServicosList() {
        return ordemDeServicosList;
    }

    public void setOrdemDeServicosList(List<OrdemDeServicos> ordemDeServicosList) {
        this.ordemDeServicosList = ordemDeServicosList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idempre != null ? idempre.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Empresas)) {
            return false;
        }
        Empresas other = (Empresas) object;
        if ((this.idempre == null && other.idempre != null) || (this.idempre != null && !this.idempre.equals(other.idempre))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "br.com.megacenter.screens.Empresas[ idempre=" + idempre + " ]";
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        changeSupport.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        changeSupport.removePropertyChangeListener(listener);
    }
    
}
