package cn.edu.bnuz.bell.report

import org.eclipse.birt.report.engine.api.DOCRenderContext
import org.eclipse.birt.report.engine.api.DocxRenderOption
import org.eclipse.birt.report.engine.api.EXCELRenderOption
import org.eclipse.birt.report.engine.api.PDFRenderOption
import org.eclipse.birt.report.engine.api.RenderOption

import java.security.InvalidParameterException

/**
 * Created by yanglin on 2016/9/16.
 */
class ReportCommand {
    private static TYPES = [
            'pdf': 'application/pdf',
            'xlsx': 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
            'docx': 'application/vnd.openxmlformats-officedocument.wordprocessingml.document'
    ]

    String reportDesign
    String title
    String titleKey

    Map<String, Object> parameters = [:]

    private String _format = 'pdf'

    String getFormat() {
        _format
    }

    void setFormat(String value) {
        if (!TYPES.containsKey(value)) {
            throw new InvalidParameterException()
        }
        _format = value
    }


    RenderOption getRenderOption() {
        RenderOption renderOption
        switch (_format) {
            case 'pdf':
                renderOption = new PDFRenderOption()
                break
            case 'xlsx':
                renderOption = new EXCELRenderOption()
                break
            case 'docx':
                renderOption = new DocxRenderOption()
                break
            default:
                throw new InvalidParameterException()
        }
        renderOption.setOutputFormat(_format)
        return renderOption
    }

    String getContentType() {
        TYPES[_format]
    }

    String getContentDisposition() {
        if (titleKey && parameters.containsKey(titleKey)) {
            return "attachment; filename=${URLEncoder.encode(this.title, 'UTF-8')}-${parameters[titleKey]}.${_format}"
        } else {
            return "attachment; filename=${URLEncoder.encode(this.title, 'UTF-8')}.${_format}"
        }
    }
}
