document.addEventListener('DOMContentLoaded', () => {
    const location = document.getElementById('location');
    const boat = document.getElementById('boat');
    const price = document.querySelector('.total');
    const button = document.getElementById('checkout');

    const update = () => {
        if (boat.value && location.value) {
            price.style.visibility = 'visible';
            button.disabled = false;
        } else {
            price.style.visibility = 'hidden';
            button.disabled = true;
        }
    };

    location.addEventListener('change', update);
    boat.addEventListener('change', update);
    update();
});
