// Gestion du formulaire de création de publication
document.addEventListener("DOMContentLoaded", () => {

    const form = document.getElementById("publicationForm");
    const message = document.getElementById("pubMessage");

    if (!form) {
        console.warn("Formulaire de publication introuvable sur cette page.");
        return;
    }

    form.addEventListener("submit", async (e) => {
        e.preventDefault();

        const data = {
            title: document.getElementById("title").value,
            content: document.getElementById("content").value,
            userId: document.getElementById("userId").value
        };

        try {
            const response = await fetch("http://localhost:8080/publications", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(data)
            });

            if (response.ok) {
                message.style.color = "green";
                message.textContent = "Publication créée avec succès !";

                form.reset();
            } else {
                message.style.color = "red";
                message.textContent = "Erreur lors de la création de la publication.";
            }

        } catch (error) {
            console.error(error);
            message.style.color = "red";
            message.textContent = "Impossible de contacter le serveur.";
        }
    });
});
