# 阿里云通义千问VL-MAX视觉模型集成方案

本文档描述了在智慧养老平台中集成阿里云通义千问-VL-MAX视觉模型的方案，实现了文本、图像、语音交互功能。

## 1. 功能说明

- **多模态交互**：支持文本、图像输入，获取智能助手的回复
- **语音识别**：支持语音输入转换为文本
- **文本转语音**：支持将回复内容转为语音输出
- **图像分析**：支持上传图像并理解图像内容

## 2. 技术实现

### 2.1 核心服务

1. `AlibabaQianwenService`: 负责与阿里云通义千问API交互，处理文本和图像请求
2. `SpeechRecognitionService`: 提供语音识别功能
3. `TextToSpeechService`: 提供文本转语音功能

### 2.2 UI界面

`ChatScreen`: 提供聊天界面，支持文本输入、语音输入、图片上传等功能

### 2.3 数据模型

`ChatMessage`: 定义聊天消息数据结构，支持文本和图像消息类型

## 3. 使用方法

### 3.1 配置API密钥

在使用前，请先在 `AlibabaQianwenService.kt` 文件中配置阿里云API密钥：

```kotlin
// API配置
private val API_KEY = "你的阿里云API密钥" 
private val API_SECRET = "你的阿里云API密钥" 
```

### 3.2 添加必要权限

在 `AndroidManifest.xml` 文件中，确保已添加以下权限：

```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.RECORD_AUDIO" />
<uses-permission android:name="android.permission.CAMERA" />
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
```

### 3.3 使用方式

1. **文本交互**: 直接在输入框中输入文本，点击发送按钮
2. **语音交互**: 点击麦克风图标切换到语音模式，长按"按住说话"按钮进行语音输入
3. **图像交互**: 点击图片图标选择图片，添加描述文本（可选），然后发送

## 4. 注意事项

1. 使用前必须配置有效的API密钥，否则将无法访问阿里云服务
2. 图像上传需要读取外部存储权限
3. 语音识别需要麦克风权限
4. 请确保设备有稳定的网络连接，以便与阿里云服务进行通信

## 5. 高级配置

### 5.1 模型参数调整

可以在 `AlibabaQianwenService.kt` 中调整模型参数以获得更好的效果：

```kotlin
// 模型名称
private val MODEL_NAME = "qwen-vl-max" // 可以根据需要使用不同的模型版本
```

### 5.2 请求体定制

可以根据实际需求修改请求体结构，添加更多参数：

```kotlin
val requestBody = """
{
    "model": "$MODEL_NAME",
    "input": {
        "messages": [
            {
                "role": "user",
                "content": [
                    {
                        "text": "$prompt"
                    }
                ]
            }
        ]
    }
}
""".trimIndent()
```

## 6. 未来扩展

1. 添加历史对话记录保存功能
2. 增加语音输出功能，自动朗读助手回复
3. 支持更多图像处理功能，如OCR文字识别
4. 添加对话情境记忆功能 