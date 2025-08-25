// Copyright 2025 PopaiTheSailor Authors
//
// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU Affero General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU Affero General Public License for more details.
//
// You should have received a copy of the GNU Affero General Public License
// along with this program.  If not, see <https://www.gnu.org/licenses/>.

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
