document.addEventListener('DOMContentLoaded', function () {
    const stars = document.querySelectorAll('.star');
    const ratingValue = document.getElementById('rating-value');
    const averageRatingElement = document.getElementById('average-rating');
    const totalVotesElement = document.getElementById('total-votes');
    const ratingForm = document.getElementById('rating-form');


    const currentRating = parseFloat(averageRatingElement.textContent.trim());

    stars.forEach(star => {
        if (parseFloat(star.getAttribute('data-value')) <= currentRating) {
            star.classList.add('selected');
        }
    });

    stars.forEach(star => {
        star.addEventListener('click', function () {

            stars.forEach(s => s.classList.remove('selected'));

            this.classList.add('selected');
            let selectedValue = this.getAttribute('data-value');
            ratingValue.value = selectedValue;

            stars.forEach(s => {
                if (s.getAttribute('data-value') <= selectedValue) {
                    s.classList.add('selected');
                }
            });
        });
    });

    ratingForm.addEventListener('submit', function (event) {
        event.preventDefault();

        const formData = new FormData(ratingForm);
        const url = ratingForm.getAttribute('action');

        fetch(url, {
            method: 'POST',
            body: formData,
            headers: {
                'X-Requested-With': 'XMLHttpRequest'
            }
        })
            .then(response => response.json())
            .then(data => {
                if (data.success) {

                    averageRatingElement.textContent = data.newAverageRating.toFixed(1);
                    totalVotesElement.textContent = parseInt(totalVotesElement.textContent) + 1;

                    const newRating = data.newAverageRating;
                    stars.forEach(s => {
                        if (parseFloat(s.getAttribute('data-value')) <= newRating) {
                            s.classList.add('selected');
                        } else {
                            s.classList.remove('selected');
                        }
                    });
                } else {
                    console.error('Error updating rating:', data.message);
                }
            })
            .catch(error => {
                console.error('Error:', error);
            });
    });
});
