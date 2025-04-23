document.addEventListener("DOMContentLoaded", () => {
    const form = document.querySelector("form.email");
    const button = document.querySelector("form.email > button");

    form.addEventListener("submit", event => {
        event.preventDefault();

        button.disabled = true;
        button.textContent = "Submitting...";

        const formData = new URLSearchParams(new FormData(form));

        fetch(form.action, {
            method: form.method,
            body: formData,
        })
            .then(response => {
                if (response.ok) {
                    button.textContent = "Thank you";
                } else {
                    console.error("Failed to submit form:", response.status);
                    button.textContent = "Failed to save";
                }
            })
            .catch(error => {
                console.error("Error submitting form:", error);
                submitBtn.textContent = "Failed to save";
            });
    });
});
