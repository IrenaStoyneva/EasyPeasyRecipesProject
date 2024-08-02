document.addEventListener("DOMContentLoaded", function() {
    setTimeout(function() {
        var successMessage = document.getElementById("successMessage");
        if (successMessage) {
            successMessage.style.display = 'none';
        }
    }, 5000);

    setTimeout(function() {
        var loginErrorMessage = document.getElementById("loginErrorMessage");
        if (loginErrorMessage) {
            loginErrorMessage.style.display = 'none';
        }
    }, 5000);
});
