package devOps;

// Importations nécessaires
import devOps.models.UtilisateurEntity;
import devOps.repositories.UtilisateurRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class SampleDataJpaApplication {

	public static void main(String[] args) throws Exception {
		SpringApplication.run(SampleDataJpaApplication.class, args);
	}

	/**
	 * Ce bloc de code est exécuté une seule fois, juste après le démarrage de l'application.
	 * Il sert à initialiser des données dans notre base de données.
	 * Ici, nous l'utilisons pour nous assurer qu'il y a au moins un utilisateur
	 * afin que nos tests de création de publication puissent fonctionner.
	 */
	@Bean
	CommandLineRunner initialiserLaBaseDeDonnees(UtilisateurRepository utilisateurRepository) {
		return args -> {
			// On vérifie s'il y a déjà des utilisateurs dans la base de données.
			if (utilisateurRepository.count() == 0) {
				System.out.println(">>> La base de données est vide. Création d'un utilisateur de test...");

				// S'il n'y en a pas, on en crée un.
				UtilisateurEntity utilisateurDeTest = new UtilisateurEntity();
				utilisateurDeTest.setNom("Nour Testeur");
				utilisateurDeTest.setMail("nour75004@example.com");
				utilisateurDeTest.setLocalisation("Paris");

				// On sauvegarde le nouvel utilisateur.
				// La base de données lui assignera automatiquement l'ID 1 (car c'est le premier).
				utilisateurRepository.save(utilisateurDeTest);

				System.out.println(">>> Utilisateur de test créé avec succès. Son ID est 1.");
				System.out.println(">>>Localisation : " + utilisateurDeTest.getLocalisation() + " >>> Nom :" + utilisateurDeTest.getNom());
			} else {
				System.out.println(">>> La base de données contient déjà des utilisateurs. Aucune donnée de test n'a été ajoutée. <<<");
			}
		};
	}
}
