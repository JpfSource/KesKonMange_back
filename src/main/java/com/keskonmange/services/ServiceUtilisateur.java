package com.keskonmange.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.keskonmange.entities.Utilisateur;
import com.keskonmange.repository.JpaUtilisateur;

/**
 * @author fogol
 *
 */
@Service
public class ServiceUtilisateur {

	@Autowired
	JpaUtilisateur ju;


	/**
	 * Méthode qui renvoie l'Utilisateur dont l'id est passé en paramètre.
	 * @param pid
	 * @return Optional<Utilisateur>
	 */
	public Optional<Utilisateur> findById(Integer pid){
		return ju.findById(pid);
	}

	/**
	 * Méthode qui renvoie l'Utilisateur dont l'email est passé en paramètre.
	 * @param email 
	 * @return Optional<Utilisateur>
	 */
	public Optional<Utilisateur> findByEmail(String email){
		return ju.findByEmail(email);
	}

	
	/**
	 * Méthode qui renvoie la liste de tous les Utilisaters
	 * @return Iterable<Utilisateur>
	 */
	public Iterable<Utilisateur> findAll(){
		return ju.findAll();
	}

	/**
	 * Méthode qui sauvegarde (create & update) et renvoie l'Utilisateur de la base de données.
	 * @param utilisateur
	 * @return Utilisateur
	 */
	public Utilisateur save(Utilisateur utilisateur){
		return ju.save(utilisateur);
	}

	/**
	 * Méthoe qui supprime l'Utilisateur de la base de données.
	 * @param pid as id de l'Utilisateur
	 */
	public void deleteById(Integer pid){
		ju.deleteById(pid);
	}
	
	


}
