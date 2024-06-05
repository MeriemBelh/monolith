

async function loadMicrofrontendScript() {
    try {
        const response = await fetch('http://localhost:8081/app-manifest.json');
        const manifest = await response.json();

        if (manifest['main.js']) {
            const script = document.createElement('script');
            script.src = manifest['main.js'];
            script.onload = () => {
                if (typeof render === 'function') {
                    render('micro-frontend-container');
                } else {
                    console.error('Render function not found in loaded script');
                }
            };
            document.head.appendChild(script);
        } else {
            console.error('Main script not found in manifest');
        }
    } catch (error) {
        console.error('Error loading manifest:', error);
    }
}
window.addEventListener('DOMContentLoaded', (e) => {
    var accessToken = document.getElementById("token").value
    var user = document.getElementById("customer").value;
    window.token = JSON.stringify(accessToken);
    window.customer = user;
})
document.addEventListener('DOMContentLoaded', loadMicrofrontendScript);


