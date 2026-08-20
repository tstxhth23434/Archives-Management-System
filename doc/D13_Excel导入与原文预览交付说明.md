# D13 交付说明 — Excel 批量导入 + 原文在线预览

> 日期：2026-08-20
> 状态：✅ 完成并联调验证通过

## 一、本次交付总览

| 模块 | 内容 |
|---|---|
| Excel 批量导入 | Hutool 读取 Excel,逐行校验,错误行精确反馈,档号自动生成 |
| 原文在线预览 | 图片直接展示,PDF 用浏览器内置查看器(iframe) |

## 二、Excel 批量导入

**接口**:`POST /api/archive/file/import`(multipart,权限 archive:file:add,@OpLog)

**模板列**(第一行为表头):
`全宗代码、门类代码、题名、责任者、文件日期、年度、保管期限、密级、关键词、页数、摘要`

**校验规则**(逐行,失败收集不中断):
- 全宗代码存在、门类代码存在且属于该全宗
- 题名必填、年度必填且为数字
- 文件日期支持 `2023-01-15` / `2023/01/15` / `20230115` 三种格式
- 档号自动生成(全宗号-门类代码-年度-四位序号,序号不复用)

**返回**:`ImportResultVO{total, success, fail, errors:[{row, message}]}`(row 为 Excel 行号,表头第 1 行,数据从第 2 行)

## 三、原文在线预览

**接口**:`GET /api/archive/electronic/preview/{id}`(权限 archive:file:query)
- 按文件后缀推断 MediaType,inline 返回(不设 attachment)
- 图片:前端 `<img>` 展示(blob URL)
- PDF 及其他:前端 `<iframe>` 展示(浏览器内置查看器)

## 四、验证结果

| 用例 | 结果 |
|---|---|
| 导入 5 行(2 成功 3 错误) | ✅ success=2 fail=3,错误行精确反馈(行4 全宗不存在/行5 门类不存在/行6 题名空) |
| 档号生成 | ✅ JSXY-WS-2025-0005(序号含已删记录不复用) |
| 预览 pdf | ✅ HTTP 200 content-type=application/pdf |
| 前端 | ✅ Vite 编译零错误,5173 代理导入/预览全通过 |

## 五、踩坑记录

- **hutool-all 不含 Excel**:需单独加 `hutool-poi` + **`poi-ooxml`(>=4.1.2)**;缺 poi-ooxml 报 `DependencyException: You need to add dependency of 'poi-ooxml'`
- PowerShell 5.1 不支持 `-Form`,上传测试用 `curl.exe -F`
- 前端 5173 后台任务可能被宿主环境 kill,验证前先确认端口

## 六、下一步（D14）

- D14：缓冲 + 联调（著录→上传→树过滤→详情串一遍），修 bug，补操作日志注解
