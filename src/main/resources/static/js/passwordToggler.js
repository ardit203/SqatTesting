document.querySelectorAll(".toggle-password")
    .forEach(t => {
        t.addEventListener('click', () => {
            const passwordInput = t.parentElement.getElementsByTagName("input")[0]
            const hidden = passwordInput.type === 'password';
            passwordInput.type = hidden ? 'text' : 'password';

            // swap icon
            const icon = t.getElementsByTagName("i")[0]
            icon.classList.toggle('bi-eye', !hidden);
            icon.classList.toggle('bi-eye-slash', hidden);
        });
    })