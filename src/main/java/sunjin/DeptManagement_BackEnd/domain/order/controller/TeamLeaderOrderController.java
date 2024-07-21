package sunjin.DeptManagement_BackEnd.domain.order.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import sunjin.DeptManagement_BackEnd.domain.order.dto.request.ApproveOrDeniedRequestDTO;
import sunjin.DeptManagement_BackEnd.domain.order.dto.request.createOrderRequestDTO;
import sunjin.DeptManagement_BackEnd.domain.order.dto.response.DepartmentInfoResponseDTO;
import sunjin.DeptManagement_BackEnd.domain.order.dto.response.GetOrderDetailResponseDTO;
import sunjin.DeptManagement_BackEnd.domain.order.dto.response.ProgressOrdersResponseDTO;
import sunjin.DeptManagement_BackEnd.domain.order.service.CommonOrderService;
import sunjin.DeptManagement_BackEnd.domain.order.service.TeamLeaderOrderService;
import sunjin.DeptManagement_BackEnd.global.error.exception.BusinessException;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class TeamLeaderOrderController {
    private final CommonOrderService commonOrderService;
    private final TeamLeaderOrderService teamLeaderOrderService;

    @PostMapping("/teamleader/orders")
    @Operation(summary = "[팀장] 주문 신청")
    public ResponseEntity<String> createOrder(@RequestPart(required = false, name = "image") MultipartFile image,
                                              @RequestPart(name = "request") @Valid createOrderRequestDTO createOrderRequestDTO){
        commonOrderService.createOrder(image, createOrderRequestDTO);
        return ResponseEntity.ok("주문에 성공했습니다.");
    }

    @GetMapping("/teamleader/{orderId}")
    @Operation(summary = "[팀장] 본인의 주문 상세 조회")
    public ResponseEntity<GetOrderDetailResponseDTO> getOrder(@PathVariable("orderId") Long orderId) throws IOException {
        GetOrderDetailResponseDTO response = commonOrderService.getOrderDetails(orderId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/teamleader/submit")
    @Operation(summary = "[팀장] 본인의 주문을 센터장에게 상신")
    public ResponseEntity<String> submitOrder(@RequestParam(value = "order", required = false) List<Long> ids){
        teamLeaderOrderService.submitOrder(ids);
        return ResponseEntity.ok("상신에 성공했습니다.");
    }

    @GetMapping("/teamleader/orders")
    @Operation(summary = "[팀장] 본인의 주문을 전체 및 상태별 조회")
    public ResponseEntity<List<?>> getAllOrder(@RequestParam(value = "status", required = false) List<String> statuses) {
        List<?> response = commonOrderService.getOrders(statuses);
        return ResponseEntity.ok(response);
    }

    // 조회 버튼 클릭
    @GetMapping("/teamleader/department")
    @Operation(summary = "[팀장] 부서 이름, 사원 이름 리턴 -> 드롭다운에 적용", description = "조회 버튼을 클릭하면 부서 이름, 사원 이름을 리턴합니다")
    public ResponseEntity<DepartmentInfoResponseDTO> getDepartment(){
        DepartmentInfoResponseDTO departmentResponseDTO = teamLeaderOrderService.getDepartmentInfo();
        return ResponseEntity.ok(departmentResponseDTO);
    }

    @GetMapping("/teamleader/department/details")
    @Operation(summary = "[팀장] 사원명, 상태를 적절히 골라 조회 진행 후 DTO 리턴")
    public ResponseEntity<List<?>> getDepartmentDetails(
            @RequestParam(value = "member", required = false) Long memberId,
            @RequestParam(value = "status", required = false) List<String> statuses){
        List<?> response = teamLeaderOrderService.getDepartmentDetails(memberId, statuses);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/teamleader/department/progress")
    @Operation(summary = "[팀장] 사원이 팀장에게 상신한 목록들 가져옴")
    public ResponseEntity<List<ProgressOrdersResponseDTO>> getFirstProgressOrders() {
        List<ProgressOrdersResponseDTO> response = teamLeaderOrderService.getFirstProgressOrders();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/temleader/department/submit/{orderId}")
    @Operation(summary = "[팀장] 사원의 주문을 승인/반려 처리함")
    public ResponseEntity<String> approveOrRejectOrderByTeamLeader(@PathVariable("orderId") Long orderId, @RequestBody ApproveOrDeniedRequestDTO approveOrDeniedRequestDTO) {
        teamLeaderOrderService.approveOrRejectOrderByTeamLeader(orderId, approveOrDeniedRequestDTO);
        return ResponseEntity.ok("처리가 완료되었습니다.");
    }

    @GetMapping("/teamleader/img/{orderId}")
    @Operation(summary = "[팀장] 수정 모달에 띄울 이미지 리턴", description = "수정 버튼을 클릭하면 해당 주문의 사진을 리턴합니다")
    public ResponseEntity<Resource> getImg(@PathVariable("orderId") Long orderId) {
        try {
            Resource resource = commonOrderService.getImg(orderId);

            // 파일이 존재하고 읽을 수 있는 경우 리턴
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG) // 이미지 타입에 따라 적절히 변경
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);
        } catch (BusinessException | IOException e) {
            // BusinessException이 발생하면 예외 처리
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        }
    }

    @PatchMapping("/teamleader/{orderId}")
    @Operation(summary = "[팀장] 본인의 주문 수정")
    public ResponseEntity<String> updateOrder(@RequestPart(name = "image") MultipartFile image,
                                              @RequestPart(name = "request") @Valid createOrderRequestDTO createOrderRequestDTO,
                                              @PathVariable("orderId") Long orderId){
        commonOrderService.updateOrder(image, createOrderRequestDTO, orderId);
        return ResponseEntity.ok("주문 수정에 성공했습니다.");
    }

    @DeleteMapping("/teamleader/{orderId}")
    @Operation(summary = "[팀장] 본인의 주문 삭제")
    public ResponseEntity<String> deleteOrder(@PathVariable("orderId") Long orderId){
        commonOrderService.deleteOrder(orderId);
        return ResponseEntity.ok("주문 삭제에 성공했습니다.");
    }
}
