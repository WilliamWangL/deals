package com.river.module.tracking.controller.app;

import com.river.module.tracking.service.PostbackService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

/**
 * Postback 回调接口
 * 用于接收联盟网络的转化回调通知
 */
@Tag(name = "公开接口 - Postback 回调")
@RestController
@RequestMapping("/api/postback")
@Validated
@Slf4j
public class PostbackController {

    @Resource
    private PostbackService postbackService;

    @GetMapping("/{networkCode}")
    @Operation(summary = "接收联盟转化回调")
    @Parameters({
            @Parameter(name = "networkCode", description = "联盟编码", required = true, example = "admitad"),
            @Parameter(name = "click_id", description = "点击 ID", example = "01HXYZ..."),
            @Parameter(name = "transaction_id", description = "联盟转化 ID（外部转化 ID）", required = true),
            @Parameter(name = "type", description = "转化类型：sale/lead", example = "sale"),
            @Parameter(name = "amount", description = "佣金金额", example = "10.50"),
            @Parameter(name = "currency", description = "货币代码", example = "USD"),
            @Parameter(name = "sig", description = "签名（可选）")
    })
    public String handlePostback(
            @PathVariable("networkCode") String networkCode,
            @RequestParam(value = "click_id", required = false) String clickId,
            @RequestParam(value = "transaction_id") String transactionId,
            @RequestParam(value = "type", required = false, defaultValue = "sale") String type,
            @RequestParam(value = "amount", required = false) BigDecimal amount,
            @RequestParam(value = "currency", required = false, defaultValue = "USD") String currency,
            @RequestParam(value = "sig", required = false) String signature,
            HttpServletRequest request) {

        log.info("[handlePostback][networkCode={}, clickId={}, transactionId={}, type={}, amount={}, currency={}]",
                networkCode, clickId, transactionId, type, amount, currency);

        try {
            postbackService.processPostback(networkCode, clickId, transactionId, type, amount, currency, signature, request);
        } catch (Exception e) {
            log.error("[handlePostback][处理失败] networkCode={}, transactionId={}, error={}",
                    networkCode, transactionId, e.getMessage(), e);
        }

        // 无论结果如何都返回 200，防止联盟重试
        return "OK";
    }

}
