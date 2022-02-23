package com.keskonmange.entities;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.keskonmange.enums.Activite;
import com.keskonmange.enums.Genre;
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
	
	
	@Column(name = "PWD", nullable = false)
	private String pwd;

	@Basic
	@Column(name = "ROLE", length = 20, nullable = true, unique = false)
	private String roleLibelle;

	@Transient
	private Role role;

	
	public Utilisateur() {}
	
	public Utilisateur(@NotNull @NotBlank String nom, @NotNull @NotBlank String prenom,String email, String pwd) {
		super(nom, prenom);
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
	
	
	/**
	 * @return the roleLibelle
	 */
	public String getRoleLibelle() {
		return roleLibelle;	
	}

	/**
	 * @param roleLibelle the roleLibelle to set
	 */
	public void setRoleLibelle(String roleLibelle) {
		this.roleLibelle = roleLibelle;
	}

	/**
	 * @return the role
	 */
	public Role getRole() {
		return role;
	}

	/**
	 * @param role the role to set
	 */
	public void setRole(Role role) {
		this.role = role;
	}

	/* PERSISTENT METHODS */
	@PostLoad
	void fillTransient() {
		if (!roleLibelle.isEmpty()) {
			this.role = Role.of(roleLibelle);
		}
	}

	@PrePersist
	void fillPersistent() {
		if (role != null) {
			this.roleLibelle = this.role.getLibelle();
		}
	}
	
	
}
