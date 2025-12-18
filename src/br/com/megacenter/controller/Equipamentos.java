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

package br.com.megacenter.controller;
import javax.swing.GroupLayout;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 *
 * @author JANDERSON
 */
@Entity
@Table(name = "equipamentos", catalog = "dbmegacenter", schema = "")
@NamedQueries({
    @NamedQuery(name = "Equipamentos.findAll", query = "SELECT e FROM Equipamentos e"),
    @NamedQuery(name = "Equipamentos.findByEtiquetaEquip", query = "SELECT e FROM Equipamentos e WHERE e.etiquetaEquip = :etiquetaEquip"),
    @NamedQuery(name = "Equipamentos.findByTipo", query = "SELECT e FROM Equipamentos e WHERE e.tipo = :tipo"),
    @NamedQuery(name = "Equipamentos.findByDescricao", query = "SELECT e FROM Equipamentos e WHERE e.descricao = :descricao"),
    @NamedQuery(name = "Equipamentos.findBySetor", query = "SELECT e FROM Equipamentos e WHERE e.setor = :setor"),
    @NamedQuery(name = "Equipamentos.findByFuncionario", query = "SELECT e FROM Equipamentos e WHERE e.funcionario = :funcionario"),
    @NamedQuery(name = "Equipamentos.findByValor", query = "SELECT e FROM Equipamentos e WHERE e.valor = :valor"),
    @NamedQuery(name = "Equipamentos.findByColuna8", query = "SELECT e FROM Equipamentos e WHERE e.coluna8 = :coluna8")})
public class Equipamentos implements Serializable {
    @Transient
    private PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "etiqueta_equip")
    private Integer etiquetaEquip;
    @Column(name = "tipo")
    private String tipo;
    @Column(name = "descricao")
    private String descricao;
    @Column(name = "setor")
    private String setor;
    @Column(name = "funcionario")
    private String funcionario;
    @Column(name = "valor")
    private String valor;
    @Column(name = "coluna8")
    private String coluna8;

    public Equipamentos() {
    }

    public Equipamentos(Integer etiquetaEquip) {
        this.etiquetaEquip = etiquetaEquip;
    }

    public Integer getEtiquetaEquip() {
        return etiquetaEquip;
    }

    public void setEtiquetaEquip(Integer etiquetaEquip) {
        Integer oldEtiquetaEquip = this.etiquetaEquip;
        this.etiquetaEquip = etiquetaEquip;
        changeSupport.firePropertyChange("etiquetaEquip", oldEtiquetaEquip, etiquetaEquip);
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        String oldTipo = this.tipo;
        this.tipo = tipo;
        changeSupport.firePropertyChange("tipo", oldTipo, tipo);
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        String oldDescricao = this.descricao;
        this.descricao = descricao;
        changeSupport.firePropertyChange("descricao", oldDescricao, descricao);
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

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        String oldValor = this.valor;
        this.valor = valor;
        changeSupport.firePropertyChange("valor", oldValor, valor);
    }

    public String getColuna8() {
        return coluna8;
    }

    public void setColuna8(String coluna8) {
        String oldColuna8 = this.coluna8;
        this.coluna8 = coluna8;
        changeSupport.firePropertyChange("coluna8", oldColuna8, coluna8);
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (etiquetaEquip != null ? etiquetaEquip.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Equipamentos)) {
            return false;
        }
        Equipamentos other = (Equipamentos) object;
        if ((this.etiquetaEquip == null && other.etiquetaEquip != null) || (this.etiquetaEquip != null && !this.etiquetaEquip.equals(other.etiquetaEquip))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "br.com.megacenter.screens.Equipamentos[ etiquetaEquip=" + etiquetaEquip + " ]";
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        changeSupport.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        changeSupport.removePropertyChangeListener(listener);
    }
    
}
