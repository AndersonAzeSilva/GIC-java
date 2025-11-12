/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.megacenter.controller;
import br.com.megacenter.controller.Empresas;
import javax.swing.GroupLayout;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

/**
 *
 * @author JANDERSON
 */
@Entity
@Table(name = "ordem_de_servicos", catalog = "dbmegacenter", schema = "")
@NamedQueries({
    @NamedQuery(name = "OrdemDeServicos.findAll", query = "SELECT o FROM OrdemDeServicos o"),
    @NamedQuery(name = "OrdemDeServicos.findByIdos", query = "SELECT o FROM OrdemDeServicos o WHERE o.idos = :idos"),
    @NamedQuery(name = "OrdemDeServicos.findByData", query = "SELECT o FROM OrdemDeServicos o WHERE o.data = :data"),
    @NamedQuery(name = "OrdemDeServicos.findByCodigoequi", query = "SELECT o FROM OrdemDeServicos o WHERE o.codigoequi = :codigoequi"),
    @NamedQuery(name = "OrdemDeServicos.findByEquipamento", query = "SELECT o FROM OrdemDeServicos o WHERE o.equipamento = :equipamento"),
    @NamedQuery(name = "OrdemDeServicos.findBySetor", query = "SELECT o FROM OrdemDeServicos o WHERE o.setor = :setor"),
    @NamedQuery(name = "OrdemDeServicos.findByFuncionario", query = "SELECT o FROM OrdemDeServicos o WHERE o.funcionario = :funcionario"),
    @NamedQuery(name = "OrdemDeServicos.findByDefeito", query = "SELECT o FROM OrdemDeServicos o WHERE o.defeito = :defeito"),
    @NamedQuery(name = "OrdemDeServicos.findByServico", query = "SELECT o FROM OrdemDeServicos o WHERE o.servico = :servico"),
    @NamedQuery(name = "OrdemDeServicos.findByStatus", query = "SELECT o FROM OrdemDeServicos o WHERE o.status = :status")})
public class OrdemDeServicos implements Serializable {
    @Transient
    private PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "idos")
    private Integer idos;
    @Column(name = "data")
    @Temporal(TemporalType.TIMESTAMP)
    private Date data;
    @Basic(optional = false)
    @Column(name = "codigoequi")
    private String codigoequi;
    @Basic(optional = false)
    @Column(name = "equipamento")
    private String equipamento;
    @Basic(optional = false)
    @Column(name = "setor")
    private String setor;
    @Basic(optional = false)
    @Column(name = "funcionario")
    private String funcionario;
    @Basic(optional = false)
    @Column(name = "defeito")
    private String defeito;
    @Basic(optional = false)
    @Column(name = "servico")
    private String servico;
    @Basic(optional = false)
    @Column(name = "status")
    private String status;
    @JoinColumn(name = "idempre", referencedColumnName = "idempre")
    @ManyToOne(optional = false)
    private Empresas idempre;

    public OrdemDeServicos() {
    }

    public OrdemDeServicos(Integer idos) {
        this.idos = idos;
    }

    public OrdemDeServicos(Integer idos, String codigoequi, String equipamento, String setor, String funcionario, String defeito, String servico, String status) {
        this.idos = idos;
        this.codigoequi = codigoequi;
        this.equipamento = equipamento;
        this.setor = setor;
        this.funcionario = funcionario;
        this.defeito = defeito;
        this.servico = servico;
        this.status = status;
    }

    public Integer getIdos() {
        return idos;
    }

    public void setIdos(Integer idos) {
        Integer oldIdos = this.idos;
        this.idos = idos;
        changeSupport.firePropertyChange("idos", oldIdos, idos);
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        Date oldData = this.data;
        this.data = data;
        changeSupport.firePropertyChange("data", oldData, data);
    }

    public String getCodigoequi() {
        return codigoequi;
    }

    public void setCodigoequi(String codigoequi) {
        String oldCodigoequi = this.codigoequi;
        this.codigoequi = codigoequi;
        changeSupport.firePropertyChange("codigoequi", oldCodigoequi, codigoequi);
    }

    public String getEquipamento() {
        return equipamento;
    }

    public void setEquipamento(String equipamento) {
        String oldEquipamento = this.equipamento;
        this.equipamento = equipamento;
        changeSupport.firePropertyChange("equipamento", oldEquipamento, equipamento);
    }

    public String getSetor() {
        return setor;
    }

    public void setSetor(String setor) {
        String oldSetor = this.setor;
        this.setor = setor;
        changeSupport.firePropertyChange("setor", oldSetor, setor);
    }

    public String getFuncionario() {
        return funcionario;
    }

    public void setFuncionario(String funcionario) {
        String oldFuncionario = this.funcionario;
        this.funcionario = funcionario;
        changeSupport.firePropertyChange("funcionario", oldFuncionario, funcionario);
    }

    public String getDefeito() {
        return defeito;
    }

    public void setDefeito(String defeito) {
        String oldDefeito = this.defeito;
        this.defeito = defeito;
        changeSupport.firePropertyChange("defeito", oldDefeito, defeito);
    }

    public String getServico() {
        return servico;
    }

    public void setServico(String servico) {
        String oldServico = this.servico;
        this.servico = servico;
        changeSupport.firePropertyChange("servico", oldServico, servico);
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        String oldStatus = this.status;
        this.status = status;
        changeSupport.firePropertyChange("status", oldStatus, status);
    }

    public Empresas getIdempre() {
        return idempre;
    }

    public void setIdempre(Empresas idempre) {
        Empresas oldIdempre = this.idempre;
        this.idempre = idempre;
        changeSupport.firePropertyChange("idempre", oldIdempre, idempre);
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idos != null ? idos.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof OrdemDeServicos)) {
            return false;
        }
        OrdemDeServicos other = (OrdemDeServicos) object;
        if ((this.idos == null && other.idos != null) || (this.idos != null && !this.idos.equals(other.idos))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "br.com.megacenter.screens.OrdemDeServicos[ idos=" + idos + " ]";
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        changeSupport.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        changeSupport.removePropertyChangeListener(listener);
    }
    
}
