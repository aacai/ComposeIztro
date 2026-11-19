# 本项目零反射、零 kotlinx.serialization、零 JNI；Compose 与 tyme4kt 均声明 safe to shrink，
# AGP 默认规则已覆盖枚举/@Keep/Parcelable/androidx，因此这里只补默认规则没有的东西。

# 保留行号，线上堆栈可用 mapping.txt 还原；SourceFile 统一改名，不泄露源文件名。
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# 禁止保留 @kotlin.Metadata：AGP 默认不保留 RuntimeInvisibleAnnotations，
# 一旦写成 -keepattributes *Annotation* 会把元数据留在 dex 里，
# 而当前 R8 无法解析 Kotlin 2.4 元数据，等价于把原始类名明文打包出去。
