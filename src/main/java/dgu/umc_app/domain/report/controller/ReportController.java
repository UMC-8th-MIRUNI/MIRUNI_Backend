package dgu.umc_app.domain.report.controller;

import dgu.umc_app.domain.report.dto.response.StoragePageResponse;
import dgu.umc_app.domain.report.service.ReportQueryService;
import dgu.umc_app.global.authorize.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/report")
@RequiredArgsConstructor
public class ReportController implements ReportApi{

    private final ReportQueryService reportQueryService;

    //보관함 페이지 조회(땅콩 갯수, 오픈 퍼센트, 이번달 리포트 오픈 여부)
    @GetMapping("/storage")
    public StoragePageResponse getStoragePage(CustomUserDetails userDetails, @RequestParam int year, @RequestParam int month) {
        Long userId = userDetails.getId();
        return reportQueryService.getStoragePage(userId, year, month);
    }

}
