// Firebase Configuration
// REPLACE THESE VALUES WITH YOUR ACTUAL FIREBASE CONFIG
const firebaseConfig = {
    apiKey: "AIzaSyDKvOWksz5Qj2r096PLn61nxc419LTXeuo",
    authDomain: "bharat-krishi-project.firebaseapp.com",
    projectId: "bharat-krishi-project",
    storageBucket: "bharat-krishi-project.firebasestorage.app",
    messagingSenderId: "625734308476",
    appId: "1:625734308476:web:08c580840355db9a216ff9",
    measurementId: "G-WSS8W38VZG"
};

// Initialize Firebase
try {
    firebase.initializeApp(firebaseConfig);
    const db = firebase.firestore();
    console.log("Firebase Initialized");

    // Initialize Map
    // Start centered on India
    const map = L.map('map').setView([20.5937, 78.9629], 5);

    // Add OpenStreetMap Tile Layer
    L.tileLayer('https://tile.openstreetmap.org/{z}/{x}/{y}.png', {
        maxZoom: 19,
        attribution: '&copy; <a href="http://www.openstreetmap.org/copyright">OpenStreetMap</a>'
    }).addTo(map);

    // Mock Data removed as per user request to start clean.

    // Layer Group for easy clearing
    const markersLayer = L.layerGroup().addTo(map);

    function addMarker(lat, lng, disease, confidence) {
        let color = 'yellow';
        let fillColor = '#ffbb33';
        let radius = 250; // Meters (Halved again)

        let riskLabel = "Critical";

        if (disease === 'Healthy') {
            color = 'green';
            fillColor = '#00C851';
            riskLabel = "Safe";
        } else if (confidence < 0.7 || disease === 'Unknown') {
            color = 'orange'; // Warning/Medium Confidence
            fillColor = '#FF8800';
            radius = 500;
            riskLabel = "Potential Risk";
        } else {
            // High Confidence Disease
            color = 'red';
            fillColor = '#ff4444';
            radius = 1000; // Urgent
            riskLabel = "Critical";
        }

        // Add Circle (Heatmap styling)
        L.circle([lat, lng], {
            color: color,
            fillColor: fillColor,
            fillOpacity: 0.4,
            radius: radius
        }).addTo(markersLayer);

        // Add Marker with Popup
        L.marker([lat, lng]).addTo(markersLayer)
            .bindPopup(`
                <b>${disease}</b><br>
                <b>Status:</b> ${riskLabel}<br>
                Confidence: ${(confidence * 100).toFixed(1)}%<br>
                Lat: ${lat.toFixed(4)}, Lng: ${lng.toFixed(4)}
            `);
    }

    // Real-Time Listener Function
    function setupRealtimeListener() {
        console.log("Listening for real-time updates...");
        document.getElementById('alert-count').innerText = "Live...";

        // onSnapshot listens for changes automatically
        db.collection("detections").onSnapshot((querySnapshot) => {
            markersLayer.clearLayers(); // Clear old markers to prevent duplicates

            let activeAlerts = 0;
            let totalScans = 0;

            querySnapshot.forEach((doc) => {
                const data = doc.data();
                if (data.latitude && data.longitude) {
                    addMarker(data.latitude, data.longitude, data.diseaseName, data.confidence);
                    totalScans++;
                    if (data.diseaseName !== 'Healthy' && data.diseaseName !== 'Unknown') {
                        activeAlerts++;
                    }
                }
            });

            document.getElementById('alert-count').innerText = activeAlerts + " Critical";
            document.getElementById('scan-count').innerText = totalScans + " Total";
        }, (error) => {
            console.error("Error getting real-time updates: ", error);
        });
    }

    // Function to clear all history from Firebase
    function clearHistory() {
        if (!confirm("⚠️ Are you sure you want to DELETE ALL detection history?\nThis cannot be undone.")) return;

        db.collection("detections").get().then((snapshot) => {
            if (snapshot.size === 0) {
                alert("No history to clear.");
                return;
            }

            const batch = db.batch();
            snapshot.docs.forEach((doc) => {
                batch.delete(doc.ref);
            });

            batch.commit().then(() => {
                console.log("History cleared.");
                markersLayer.clearLayers();
                alert("Map history cleared!");
            }).catch((error) => {
                console.error("Error clearing history: ", error);
                alert("Error clearing history: " + error.message);
            });
        });
    }

    // Assign to window for manual button
    window.refreshData = setupRealtimeListener;
    window.clearFirebaseData = clearHistory;

    // Initial Load
    setupRealtimeListener();

} catch (e) {
    console.error("Firebase Error (Check Config):", e);
    alert("Please update app.js with your Firebase Config!");
}
