# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
-keepclassmembers class fqcn.of.javascript.interface.for.webview {
   public *;
}

-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

-keep class kotlinx.coroutines.android.** {*;}

# Gson uses generic type information stored in a class file when working with fields. Proguard
# removes such information by default, so configure it to keep all of it.
-keepattributes Signature

# Gson specific classes
-keep class sun.misc.Unsafe { *; }
#-keep class com.google.gson.stream.** { *; }

# Application classes that will be serialized/deserialized over Gson
-keep class com.cbi.app.trs.** { *; }
-keep class vn.payoo.** { *; }

# proguard for webex SDK
-keep class com.ciscowebex.** { *; }
-keep class com.webex.** { *; }
-keep class com.cisco.** { *; }
-keep class org.sqlite.database.sqlite.** { *; }
-keep class org.bouncycastle.** { *; }

#Proguard rules for using green robot eventbus.
#'http://greenrobot.org/eventbus/'
-keepattributes *Annotation*
-keepclassmembers class ** {
    @org.greenrobot.eventbus.Subscribe <methods>;
}
-keep enum org.greenrobot.eventbus.ThreadMode { *; }

# Only required if you use AsyncExecutor
-keepclassmembers class * extends org.greenrobot.eventbus.util.ThrowableFailureEvent {
    <init>(java.lang.Throwable);
}

#Support payoo
-keepclassmembers enum * { *; }

-keep class com.google.android.gms.** { *; }
-keep class com.facebook.applinks.** { *; }
-keepclassmembers class com.facebook.applinks.** { *; }
-keep class com.facebook.FacebookSdk { *; }
-keep class com.huawei.hms.ads.** { *; }
-keep interface com.huawei.hms.ads.** { *; }

-keep class com.appsflyer.** { *; }
-keep public class com.android.installreferrer.** { *; }

# ------------------ OkHttp --------------------------
-dontwarn okio.**
-dontwarn okhttp3.**
-dontwarn javax.annotation.**
-dontwarn org.conscrypt.**
-keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase

# ------------------ SQLCipher ------------------------
# If you're only using Chat, you can remove the sqlcipher rules
-keep class net.sqlcipher.** { *; }
-dontwarn net.sqlcipher.**

# ------------------ Gson ----------------------------
-keepclassmembers,allowobfuscation class * {
    @com.google.gson.annotations.SerializedName <fields>;
}

############### SOS ONLY ###############
# You can delete this section if you are not using SOS

-dontwarn lombok.**

# Our components are initialized using reflection and can appear to be unused
-keepclassmembers class * implements com.salesforce.android.sos.component.Component

# ------------------ Eventbus SOS ------------------------
# The onEvent methods are called from the EventBus library and can appear unused.
-keepclassmembers class com.salesforce.android.sos.** {
    public void onEvent(...);
}

# ------------------ Opentok SOS -------------------------
# OpenTok cannot handle any code stripping for optimization.
-keep class com.opentok.** { *; }
-keep class org.webrtc.** { *; }
-keep class com.salesforce.android.sos.** { *; }

# ------------------ Gson SOS ----------------------------
# Preserve the special static methods that are required in all enumeration classes.
# We use these predominantly for serializing enums with Gson.
-keepclassmembers enum com.salesforce.android.sos.** {
    **[] $VALUES;
    public *;
}

# Open In-App Billing
# GOOGLE
-keep class com.android.vending.billing.**

# AMAZON
-dontwarn com.amazon.**
-keep class com.amazon.** {*;}
-keepattributes *Annotation*
-dontoptimize

# SAMSUNG
-keep class com.sec.android.iap.**

# NOKIA
-keep class com.nokia.payment.iap.aidl.**

#FORTUMO
-keep class mp.** { *; }

-keepattributes InnerClasses
 -keep class **.R
 -keep class **.R$* {
    <fields>;
}

# Retrofit does reflection on generic parameters. InnerClasses is required to use Signature and
# EnclosingMethod is required to use InnerClasses.
-keepattributes Signature, InnerClasses, EnclosingMethod

# Retrofit does reflection on method and parameter annotations.
-keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations

# Keep annotation default values (e.g., retrofit2.http.Field.encoded).
-keepattributes AnnotationDefault

# Retain service method parameters when optimizing.
-keepclassmembers,allowshrinking,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}

# Ignore annotation used for build tooling.
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement

# Ignore JSR 305 annotations for embedding nullability information.
-dontwarn javax.annotation.**

# Guarded by a NoClassDefFoundError try/catch and only used when on the classpath.
-dontwarn kotlin.Unit

# Top-level functions that can only be used by Kotlin.
-dontwarn retrofit2.KotlinExtensions
-dontwarn retrofit2.KotlinExtensions$*

# With R8 full mode, it sees no subtypes of Retrofit interfaces since they are created with a Proxy
# and replaces all potential values with null. Explicitly keeping the interfaces prevents this.
-if interface * { @retrofit2.http.* <methods>; }
-keep,allowobfuscation interface <1>

# Keep generic signature of Call, Response (R8 full mode strips signatures from non-kept items).
-keep,allowobfuscation,allowshrinking interface retrofit2.Call
-keep,allowobfuscation,allowshrinking class retrofit2.Response

# With R8 full mode generic signatures are stripped for classes that are not
# kept. Suspend functions are wrapped in continuations where the type argument
# is used.
-keep,allowobfuscation,allowshrinking class kotlin.coroutines.Continuation