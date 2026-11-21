تطبيق أبو عدنان - مشروع أندرويد
================================
أولًا: طلع ملف APK (اختار طريقة واحدة)

الطريقة 1: Android Studio (على الكمبيوتر)
1) ثبّت Android Studio (مجاني). 2) File > Open واختار المجلد ده وانتظر الـSync.
3) Build > Build Bundle(s) / APK(s) > Build APK(s).
4) الملف: app/build/outputs/apk/debug/app-debug.apk (ابعته للموبايل وثبّته).

الطريقة 2: البناء أونلاين من غير ما تثبّت حاجة (GitHub)
1) اعمل حساب على github.com وأنشئ Repository جديد (Private).
2) ارفع كل محتويات المجلد ده (مع مجلد .github).
3) من تبويب Actions اختار "Build APK" ثم Run workflow.
4) بعد دقايق نزّل ملف abu-adnan-apk من نفس الصفحة.

ثانيًا: النسخة الرسمية الموقّعة (للنشر الحقيقي / Google Play)
1) اعمل مفتاح توقيع مرة واحدة واحتفظ بيه (لو ضاع مش هتقدر تحدّث التطبيق):
   keytool -genkeypair -v -keystore abu-adnan.jks -alias abuadnan -keyalg RSA -keysize 2048 -validity 10000
2) محليًا: اعمل ملف keystore.properties جنب build.gradle:
   storeFile=abu-adnan.jks
   storePassword=كلمة_السر
   keyAlias=abuadnan
   keyPassword=كلمة_السر
   وبعدها: Build > Generate Signed Bundle / APK. (APK للتوزيع المباشر، AAB لجوجل بلاي.)
3) على GitHub: ضيف في Settings > Secrets > Actions القيم: KEYSTORE_BASE64 (محتوى ملف jks بصيغة base64) و KEYSTORE_PASSWORD و KEY_ALIAS و KEY_PASSWORD، وهيطلع APK موقّع.
مهم: ماترفعش ملف jks ولا keystore.properties على GitHub (الـ.gitignore بيمنعه).

إعدادات الأمان المطبّقة
- المحتوى جوه التطبيق (assets) ومفيش تحميل للموقع من الإنترنت.
- WebView: منع الوصول لملفات الجهاز، منع المحتوى المختلط، تعطيل الموقع الجغرافي والنوافذ المنبثقة، تفعيل Safe Browsing، وقفل أدوات التصحيح.
- التنقل: الروابط الخارجية المسموحة فقط واتساب والخريطة والاتصال (tel)، وأي رابط غيرها بيتمنع.
- الموارد: الإنترنت مسموح بس لخطوط جوجل، وباقي الطلبات بتتمنع.
- سياسة CSP صارمة داخل الصفحة + منع HTTP غير المشفر (cleartext) + إلغاء النسخ الاحتياطي.
- نسخة Release بتتضغط وتتعمّى (R8) وبتتوقّع بمفتاحك.
لو زودت روابط جديدة (إنستجرام مثلًا) ضيف نطاقها في LINK_HOSTS داخل MainActivity.java.
