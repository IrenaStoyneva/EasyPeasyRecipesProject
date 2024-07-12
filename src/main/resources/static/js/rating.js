document.addEventListener('DOMContentLoaded', (event) => {
    const stars = document.querySelectorAll('.star-rating input');
    const averageRating = parseFloat(document.getElementById('average-rating').innerText);
    stars.forEach(star => {
        if (parseFloat(star.value) <= averageRating) {
            star.checked = true;
        }
    });
});
