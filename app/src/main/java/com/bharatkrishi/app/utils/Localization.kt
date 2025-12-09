package com.bharatkrishi.app.utils

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

object LocalizationManager {
    var isHindi by mutableStateOf(false)

    fun get(key: String): String {
        return if (isHindi) {
            translations[key] ?: key
        } else {
            key
        }
    }

    private val translations = mapOf(
        // Navigation & General
        "Home" to "होम", // Home
        "Pest Control" to "कीट नियंत्रण",
        "Fertilizer Advisory" to "खाद सलाह",
        "Fertilizer" to "खाद",
        "GPS Location" to "जीपीएस लोकेशन",
        "Logout" to "लॉग आउट",
        "Profile" to "प्रोफाइल",
        "Help" to "मदद",
        "Advisory" to "सलाह",
        "Login" to "लॉग इन",
        "Sign Up" to "साइन अप",
        
        // Home Screen
        "Good Morning, Farmer" to "सुप्रभात, किसान भाई",
        "Govt Schemes" to "सरकारी योजनाएं",
        "Quick Actions" to "त्वरित कार्य",
        "Weather Alert" to "मौसम चेतावनी",
        "Market Prices" to "बाजार भाव",
        "Loading..." to "लोड हो रहा है...",
        "Error" to "त्रुटि",
        "Fetching..." to "लाया जा रहा है...",
        "Welcome" to "स्वागत है",
        
        // Quick Actions
        "Wheat Detection" to "गेहूँ पहचान", 
        "AI Assistant" to "AI सहायक",
        "Drone Analysis" to "ड्रोन विश्लेषण",
        "Community Forum" to "किसान मंच",
        
        // Soil/Wheat Analysis Screen
        "Crop Scanner" to "फसल स्कैनर",
        "Soil Analysis" to "मिट्टी जाँच", 
        "Wheat Analysis" to "गेहूँ विश्लेषण",
        "Scan Your Crop" to "अपनी फसल स्कैन करें",
        "Take a photo of your crop leaves to detect diseases, pests, and nutrient deficiencies" to "बीमारियों और कीटों का पता लगाने के लिए अपनी फसल की पत्तियों की फोटो लें",
        "Open Camera & Analyze" to "कैमरा खोलें और विश्लेषण करें",
        "Select from Gallery" to "गैलरी से चुनें",
        "Loading AI model..." to "AI मॉडल लोड हो रहा है...",
        "AI Diagnosis" to "AI निदान",
        "Analyzed Image" to "विश्लेषण की गई छवि",
        "How it works" to "यह कैसे काम करता है",
        "Take Photo" to "फोटो लें",
        "Capture clear image of affected crop leaves" to "प्रभावित फसल की पत्तियों की स्पष्ट तस्वीर लें",
        "AI Analysis" to "AI विश्लेषण",
        "Our AI analyzes the image for diseases and pests" to "हमारा AI बीमारियों और कीटों के लिए छवि का विश्लेषण करता है",
        "Get Results" to "परिणाम प्राप्त करें",
        "Receive diagnosis with treatment recommendations" to "उपचार की सिफारिशों के साथ निदान प्राप्त करें",
        "Recent Scans" to "हाल के स्कैन",
        "Latest Soil Test Results" to "नवीनतम मिट्टी परीक्षण परिणाम",
        "Soil Parameters" to "मिट्टी के मापदंड",
        "Recommendations" to "सिफारिशें",
        "Schedule New Soil Test" to "नई मिट्टी जाँच बुक करें",
        "Updated 5 days ago" to "5 दिन पहले अपडेट किया गया",
        
        // Soil Params
        "pH Level" to "pH स्तर",
        "Nitrogen (N)" to "नाइट्रोजन (N)",
        "Phosphorus (P)" to "फास्फोरस (P)",
        "Potassium (K)" to "पोटेशियम (K)",
        "Organic Matter" to "जैविक पदार्थ",
        "Soil Moisture" to "मिट्टी की नमी",
        "Slightly Acidic" to "हल्का अम्लीय",
        "Medium" to "मध्यम",
        "High" to "उच्च",
        "Low" to "कम",
        "Good" to "अच्छा",
        "Optimal" to "अनुकूल",
        
        // Recommendations
        "Add potassium-rich fertilizer to improve K levels" to "K स्तर सुधारने के लिए पोटेशियम युक्त खाद डालें",
        "Consider lime application to increase pH slightly" to "pH को थोड़ा बढ़ाने के लिए चूने का प्रयोग करें",
        "Maintain current nitrogen management practices" to "वर्तमान नाइट्रोजन प्रबंधन बनाए रखें",
        "Good phosphorus levels - no action needed" to "फास्फोरस का स्तर अच्छा है - कोई कार्रवाई की आवश्यकता नहीं",

        // Market Prices Screen
        "State" to "राज्य",
        "Commodity" to "फसल/वस्तु",
        "All States" to "सभी राज्य",
        "All Commodities" to "सभी फसलें",
        "Wheat" to "गेहूँ", 
        "Onion" to "प्याज",
        "Potato" to "आलू",
        "Cotton" to "कपास",
        "Lemon" to "नींबू",
        "Sugarcane" to "गन्ना",
        "Mustard" to "सरसों",
        "No results found for this filter." to "इस फिल्टर के लिए कोई परिणाम नहीं मिला।",
        "Select filters to see prices." to "भाव देखने के लिए फिल्टर चुनें।",
        "Unknown Commodity" to "अज्ञात फसल",
        "Min" to "न्यूनतम",
        "Max" to "अधिकतम",
        "Loading prices..." to "भाव लोड हो रहे हैं...",
        "Could not load prices." to "भाव लोड नहीं हो सके।",
        
        // Diseases / Diagnosis
        "Healthy" to "स्वस्थ",
        "Yellow rust" to "पीला रतुआ",
        "Brown rust" to "भूरा रतुआ",
        "Loose Smut" to "लुज़ स्मट (कांगियारी)",
        "Septoria" to "सेप्टोरिया",
        "Stripe rust" to "धारीदार रतुआ",
        "Mildew" to "फफूंदी",
        "Early Blight Detected" to "अर्ली ब्लाइट (झुलसा) का पता चला",
        "Aphid Infestation" to "माहूँ (एफिड) का प्रकोप",
        "Prediction" to "परिणाम", 
        "Confidence" to "सटीकता", 
        "Error during inference" to "विश्लेषण में त्रुटि",
        "Unknown" to "This is not a wheat crop",
        "This is not a wheat crop" to "यह गेहूँ की फसल नहीं है",
        "Please take a different photo." to "कृपया दूसरी फोटो लें।",

        // Weather Forecast Screen
        "Weather Forecast" to "मौसम पूर्वानुमान",
        "Search City (e.g. Pune)" to "शहर खोजें (उदा. पुणे)",
        "Search" to "खोजें",
        "Could not load weather." to "मौसम लोड नहीं हो सका।",
        "Enter a location to see the forecast" to "पूर्वानुमान देखने के लिए स्थान दर्ज करें",
        "Feels Like:" to "महसूस हो रहा है:",
        "Last Updated:" to "अंतिम अपडेट:",
        "Current Details" to "वर्तमान विवरण",
        "Humidity" to "नमी",
        "Wind" to "हवा",
        "UV Index" to "UV सूचकांक",
        "Visibility" to "दृश्यता",
        "Pressure" to "दबाव",
        "Precipitation" to "वर्षा",
        "Cloud Cover" to "बादल",

        // Crop Advisory Screen
        "Crop Advisory" to "फसल सलाह",
        "Crop Recommendations" to "फसल सिफारिशें",
        "Based on your farm history, soil type, and market trends" to "आपके खेत के इतिहास, मिट्टी के प्रकार और बाजार के रुझानों के आधार पर",
        "Excellent for winter season" to "सर्दियों के मौसम के लिए उत्कृष्ट",
        "Suitable for your soil type" to "आपकी मिट्टी के प्रकार के लिए उपयुक्त",
        "Good irrigation available" to "अच्छी सिंचाई उपलब्ध है",
        "High demand, good price" to "उच्च मांग, अच्छी कीमत",
        "Premium variety available" to "प्रीमियम किस्म उपलब्ध है",
        "Processing unit nearby" to "प्रसंस्करण इकाई पास में",
        "Rabi Season" to "रबी सीजन",
        "Kharif Season" to "खरीफ सीजन",
        "Year Round" to "साल भर",

        // Fertilizer Advisory Screen
        "Quantity" to "मात्रा",
        "Timing" to "समय",
        "Sowing (Basal)" to "बुवाई (आधार)",
        "CRI Stage (20-25 DAS)" to "CRI चरण (20-25 DAS)",
        "Jointing Stage (40-45 DAS)" to "गाठ बनने का चरण (40-45 DAS)",
        "Booting Stage" to "बूटिंग चरण",
        "DAP + MOP" to "DAP + MOP",
        "Urea" to "यूरिया",
        "Zinc Sulphate (if deficiency)" to "जिंक सल्फेट (यदि कमी हो)",
        "SSP + Urea" to "SSP + यूरिया",
        "At time of sowing" to "बुवाई के समय",
        "With first irrigation" to "पहली सिंचाई के साथ",
        "With second irrigation" to "दूसरी सिंचाई के साथ",
        "If leaves show yellowing" to "यदि पत्तियां पीली दिखाई दें",
        "Basal application" to "आधार अनुप्रयोग",
        "Provides essential Phosphorus and Potassium. Potassium helps resist Rust diseases." to "आवश्यक फास्फोरस और पोटेशियम प्रदान करता है। पोटेशियम रतुआ रोगों का विरोध करने में मदद करता है।",
        "Crucial for tillering. Avoid excess Nitrogen to prevent Rust susceptibility." to "कलियाँ फूटने के लिए महत्वपूर्ण। रतुआ संवेदनशीलता को रोकने के लिए अतिरिक्त नाइट्रोजन से बचें।",
        "Supports stem elongation. Balanced nutrition reduces Mildew risk." to "तने को बढ़ने में मदद करता है। संतुलित पोषण फफूंदी के जोखिम को कम करता है।",
        "Corrects Zinc deficiency. Healthy plants resist Septoria better." to "जिंक की कमी को सही करता है। स्वस्थ पौधे सेप्टोरिया का बेहतर विरोध करते हैं।",
        "Sulphur in SSP is vital for oil content and disease resistance." to "SSP में सल्फर तेल की मात्रा और रोग प्रतिरोधक क्षमता के लिए महत्वपूर्ण है।",

        // Pest Control Screen
        "Wheat Disease Guide" to "गेहूँ रोग गाइड",
        "Yellow Rust (Stripe Rust)" to "पीला रतुआ (धारीदार रतुआ)",
        "Brown Rust (Leaf Rust)" to "भूरा रतुआ (पत्ती रतुआ)",
        "Powdery Mildew" to "पाउडरी मिल्ड्यू (फफूंदी)",
        "Yellow stripes on leaves. Affects photosynthesis. Use Propiconazole." to "पत्तियों पर पीली धारियां। प्रकाश संश्लेषण को प्रभावित करता है। प्रोपिकोनाज़ोल का प्रयोग करें।",
        "Orange-brown pustules on leaves. Spread by wind. Use Tebuconazole." to "पत्तियों पर नारंगी-भूरे रंग के दाने। हवा से फैलता है। टेबुकोनाज़ोल का प्रयोग करें।",
        "Black powdery mass replacing grain heads. Seed treatment with Carboxin is effective." to "अनाज की बालियों की जगह काला पाउडर। कार्बोक्सिन से बीज उपचार प्रभावी है।",
        "Yellow spots on leaves with black dots. Causes leaf blotch. Use fungicides." to "पत्तियों पर काले बिंदुओं के साथ पीले धब्बे। लीफ ब्लॉच का कारण बनता है। कवकनाशी का प्रयोग करें।",
        "White powdery growth on leaves. Reduces yield. Use Sulphur-based fungicides." to "पत्तियों पर सफेद पाउडर जैसी वृद्धि। उपज कम करता है। सल्फर आधारित कवकनाशी का प्रयोग करें।",
        "Similar to Yellow Rust. Causes significant yield loss in cool, moist conditions." to "पीले रतुआ के समान। ठंडी, नम स्थितियों में महत्वपूर्ण उपज हानि होती है।",
        "Risk" to "जोखिम",

        // AI Chat Screen
        "KrishiMitra AI" to "कृषि मित्र AI",
        "Online" to "ऑनलाइन",
        "Ask me anything..." to "मुझसे कुछ भी पूछें...",
        "Speak now..." to "अब बोलें...",
        "Speech recognition not supported" to "वाक् पहचान समर्थित नहीं है",
        "Permission denied" to "अनुमति अस्वीकृत",

        // Drone Analysis Screen
        "Drone Field Analysis" to "ड्रोन खेत विश्लेषण",
        "Analyze Entire Fields" to "पूरे खेत का विश्लेषण करें",
        "Upload a drone image to generate a full-field disease map." to "पूरे खेत का रोग नक्शा बनाने के लिए ड्रोन इमेज अपलोड करें।",
        "Upload Drone Image" to "ड्रोन इमेज अपलोड करें",
        "Select Image" to "इमेज चुनें",
        "Preview:" to "पूर्वावलोकन:",
        "Analyzing field…" to "खेत का विश्लेषण हो रहा है...",
        "Layers" to "परतें",
        "Disease Heatmap" to "रोग हीटमैप",
        "NDVI Map" to "NDVI नक्शा",
        "Field Health" to "खेत का स्वास्थ्य",
        "Patches Analyzed" to "विश्लेषण किए गए हिस्से",
        "Diseased" to "रोगग्रस्त",
        "Class Breakdown" to "रोग वर्गीकरण",
        "NDVI Map (Prototype)" to "NDVI नक्शा (प्रोटोटाइप)",
        "Your Field" to "आपका खेत",
        "NDVI Analysis Area" to "NDVI विश्लेषण क्षेत्र",
        "Waiting for GPS location..." to "जीपीएस लोकेशन का इंतज़ार है...",
        "Save Results to Cloud" to "परिणाम क्लाउड पर सहेजें",
        "Saving..." to "सहेजा जा रहा है...",
        "Saved to Cloud!" to "क्लाउड पर सहेजा गया!",
        "Uploading..." to "अपलोड हो रहा है...",
        "8-Class Breakdown" to "8-श्रेणी विश्लेषण",

        // Community & Help Screen
        "Community Forum Coming Soon!" to "किसान मंच जल्द आ रहा है!",
        "Help & Support" to "मदद और समर्थन",
        "Call Support" to "सपोर्ट कॉल करें",
        "Get instant help from our support team" to "हमारी सपोर्ट टीम से तुरंत मदद पाएं",
        "Email Support" to "सपोर्ट ईमेल करें",
        "Send us your queries at support@krishi.com" to "अपने प्रश्न support@krishi.com पर भेजें",

        // Notifications Screen
        "Notifications" to "सूचनाएं",
        "Mark all read" to "सभी पढ़े गए",
        "unread notifications" to "अपठित सूचनाएं",
        "Stay updated with important farm alerts" to "महत्वपूर्ण कृषि अलर्ट के साथ अपडेट रहें",
        "Heavy rain expected in 2 days. Cover your crops!" to "2 दिनों में भारी बारिश की संभावना। अपनी फसलों को ढक दें!",
        "Pest Identified" to "कीट की पहचान",
        "Aphids detected on tomatoes. Immediate action required." to "टमाटर पर माहूँ का पता चला। तत्काल कार्रवाई आवश्यक।",
        "Market Price Update" to "बाजार भाव अपडेट",
        "Wheat prices increased by 2.4% today" to "आज गेहूँ के भाव में 2.4% की वृद्धि",
        "Fertilizer Reminder" to "खाद रिमाइंडर",
        "Time to apply nitrogen fertilizer to your wheat crop" to "अपनी गेहूँ की फसल में नाइट्रोजन खाद डालने का समय",
        "Soil Test Results" to "मिट्टी परीक्षण परिणाम",
        "Your soil test results are ready. pH level: 6.5" to "आपकी मिट्टी परीक्षण रिपोर्ट तैयार है। pH स्तर: 6.5",
        "New Tutorial Available" to "नया ट्यूटोरियल उपलब्ध",
        "Learn about organic farming techniques" to "जैविक खेती तकनीकों के बारे में जानें",

        // YouTube Tutorials Screen
        "YouTube Tutorials" to "YouTube ट्यूटोरियल",
        "Duration:" to "अवधि:",
        "How to Improve Soil Fertility" to "मिट्टी की उर्वरता कैसे सुधारें",
        "Pest Management in Cotton" to "कपास में कीट प्रबंधन",
        "Best Irrigation Techniques" to "सर्वोत्तम सिंचाई तकनीकें",
        "Market Price Updates" to "बाजार भाव अपडेट",

        // Scheme Detail Screen
        "Scheme Details" to "योजना विवरण",
        "Unknown Scheme" to "अज्ञात योजना",
        "No details available." to "कोई विवरण उपलब्ध नहीं है."
    )
}
