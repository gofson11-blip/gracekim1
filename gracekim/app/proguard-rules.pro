# 사주사랑 R8 규칙
# 화면 상태 저장(rememberSaveable)에 쓰는 Serializable 클래스는 필드를 그대로 유지
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}
-keep class com.gracekim.saju.ui.FormData { *; }
-keep enum com.gracekim.saju.core.** { *; }

# 오류 보고서에서 줄 번호를 볼 수 있게
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile
