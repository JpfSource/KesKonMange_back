package com.keskonmange.entities;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.keskonmange.enums.Role;


/**
 * Classe qui définie l'utilisateur ainsi que si oui ou non il est administrateur d'un groupe.
 * 
 * @author Christian Ingold, Jean-Philippe Francisco, Steeve Dombald, Antonin Guillon.
 *
 */

@Entity
@Table(name = "UTILISATEUR")
public class Utilisateur extends Personne {

	@NotNull
	@NotBlank
	@Column(name = "EMAIL", nullable = false)
	private String email;
	
	@NotNull
	@NotBlank
	@Column(name = "PWD", nullable = false)
	private String pwd;

	@Basic
    @Column(name = "ROLE", length = 20, nullable = true, unique = false)
    private String roleLibelle;

    @Transient
    private Role role;	
	
	
	public Utilisateur() {}
	
	public Utilisateur(Integer id, @NotNull @NotBlank String nom, @NotNull @NotBlank String prenom,String email, String pwd) {
		super(id, nom, prenom);
		this.email = email;
		this.pwd = pwd;
	}

	public Utilisateur(String email, String pwd) {
		super();
		this.email = email;
		this.pwd = pwd;
	}


	public String getEmail() {
		return email;
	}

	public String getPwd() {
		return pwd;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setPwd(String pwd) {
		this.pwd = pwd;
	}
}
