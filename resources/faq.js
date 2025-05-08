document.addEventListener("DOMContentLoaded", () => {
    document.querySelectorAll(".faq").forEach((faq) => {
        let startX, startY;

        faq.addEventListener('pointerdown', (e) => {
            startX = e.clientX;
            startY = e.clientY;
        });

        faq.addEventListener('pointerup', (e) => {
            const dx = e.clientX - startX;
            const dy = e.clientY - startY;
            const moved = Math.hypot(dx, dy) > 5;
            if (!moved && e.target.tagName !== 'SUMMARY') {
                faq.open = !faq.open;
            }
        });
    });
});
