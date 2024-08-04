document.addEventListener("DOMContentLoaded", function() {
    // Функция за скриване на елемент по ID след определено време
    function hideMessageAfterTimeout(elementId, timeout) {
        setTimeout(function() {
            var element = document.getElementById(elementId);
            if (element) {
                element.style.display = 'none';
            }
        }, timeout);
    }

    // Скриване на съобщенията за успех и грешка
    hideMessageAfterTimeout("successMessage", 5000);
    hideMessageAfterTimeout("loginErrorMessage", 5000);
    hideMessageAfterTimeout("errorMessage", 5000);
    hideMessageAfterTimeout("passwordSuccess", 5000);
    hideMessageAfterTimeout("passwordError", 5000);
    hideMessageAfterTimeout("usernameSuccess", 5000);
    hideMessageAfterTimeout("recipeUpdateSuccess", 5000);
});
