// rating.js

// To access the stars
let stars = document.querySelectorAll(".star");
let output = document.getElementById("average-rating");
let ratingInput = document.getElementById("rating-value");
let selectedRating = 0; // Add a variable to store the selected rating

// Function to update rating
function gfg(n) {
    selectedRating = n; // Set the selected rating
    ratingInput.value = selectedRating; // Update the hidden input value
    remove();
    for (let i = 0; i < n; i++) {
        if (n == 1) cls = "one";
        else if (n == 2) cls = "two";
        else if (n == 3) cls = "three";
        else if (n == 4) cls = "four";
        else if (n == 5) cls = "five";
        stars[i].className = "star " + cls;
    }
    output.innerText = "Rating is: " + n + "/5";
}

// To remove the pre-applied styling
function remove() {
    let i = 0;
    while (i < 5) {
        stars[i].className = "star";
        i++;
    }
}

// Handle rating submission
document.addEventListener('DOMContentLoaded', (event) => {
    const ratingForm = document.querySelector('.rating-section form');
    if (ratingForm) {
        ratingForm.addEventListener('submit', function(event) {
            event.preventDefault();
            const formData = new FormData(ratingForm);
            fetch(ratingForm.action, {
                method: 'POST',
                body: new URLSearchParams(formData)
            })
                .then(response => response.json())
                .then(data => {
                    if (data.success) {
                        output.innerText = data.newAverageRating;
                        gfg(data.newAverageRating);
                    } else {
                        console.error('Error:', data.errors);
                    }
                })
                .catch(error => console.error('Error:', error));
        });
    }
});
