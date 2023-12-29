// Helper to resize Airtable iframe and present a loading message for it
const airtableIframe = document.getElementById('airtable-signup');
const loadingSpinner = document.getElementById('loading-spinner');

function onLoad() {
    loadingSpinner.style.display = 'none';
};

if (airtableIframe) {
    const domHeight = window.innerHeight;
    const iframeHeight = domHeight - 180;
    airtableIframe.height = iframeHeight + 'px';
    airtableIframe.onload = onLoad;
}
