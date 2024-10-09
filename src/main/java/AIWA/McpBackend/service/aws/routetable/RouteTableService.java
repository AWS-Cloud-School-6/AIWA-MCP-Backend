package AIWA.McpBackend.service.aws.routetable;

import AIWA.McpBackend.provider.aws.api.dto.routetable.RouteTableRequestDto;
import AIWA.McpBackend.service.aws.s3.S3Service;
import AIWA.McpBackend.service.terraform.TerraformService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RouteTableService {

    private final S3Service s3Service;
    private final TerraformService terraformService;

    /**
     * Route Table을 생성합니다.
     *
     * @param routeTableRequest Route Table 생성 요청 DTO
     * @param userId            사용자 ID
     * @throws Exception Route Table 생성 중 발생한 예외
     */
    public void createRouteTable(RouteTableRequestDto routeTableRequest, String userId) throws Exception {
        // 1. 새로운 Route Table .tf 파일 생성
        String routeTableTfContent = String.format("""
                resource "aws_route_table" "%s" {
                  vpc_id = aws_vpc.%s.id
                  tags = {
                    Name = "%s"
                  }
                }
                """,
                routeTableRequest.getRouteTableName(),
                routeTableRequest.getVpcName(),
                routeTableRequest.getRouteTableName());

        // 2. Route Table .tf 파일 이름 설정 (예: route_table_myRouteTable.tf)
        String routeTableTfFileName = String.format("route_table_%s.tf", routeTableRequest.getRouteTableName());

        // 3. S3에 새로운 Route Table .tf 파일 업로드
        String s3Key = "users/" + userId + "/" + routeTableTfFileName;
        s3Service.uploadFileContent(s3Key, routeTableTfContent);

        // 4. Terraform 실행 요청
        terraformService.executeTerraform(userId);
    }

    /**
     * Route Table을 삭제합니다.
     *
     * @param routeTableName Route Table 이름
     * @param userId         사용자 ID
     * @throws Exception Route Table 삭제 중 발생한 예외
     */
    public void deleteRouteTable(String routeTableName, String userId) throws Exception {
        // 1. 삭제하려는 Route Table .tf 파일 이름 설정 (예: route_table_myRouteTable.tf)
        String routeTableTfFileName = String.format("route_table_%s.tf", routeTableName);

        // 2. S3에서 해당 Route Table .tf 파일 삭제
        String s3Key = "users/" + userId + "/" + routeTableTfFileName;
        s3Service.deleteFile(s3Key);

        // 3. Terraform 실행 요청
        terraformService.executeTerraform(userId);
    }

    /**
     * Internet Gateway와 연결된 라우트를 Route Table에 추가합니다.
     *
     * @param routeTableName Route Table 이름
     * @param internetGatewayName Internet Gateway 이름
     * @param cidrBlock       CIDR 블록 (예: "0.0.0.0/0"은 모든 트래픽)
     * @param userId          사용자 ID
     * @throws Exception Route 추가 중 발생한 예외
     */
    public void addRouteToInternetGateway(String routeTableName, String internetGatewayName, String cidrBlock, String userId) throws Exception {
        // 1. Route를 추가하는 코드 블록 생성
        String routeTfContent = String.format("""
                resource "aws_route" "route_to_igw" {
                  route_table_id = aws_route_table.%s.id
                  destination_cidr_block = "%s"
                  gateway_id = aws_internet_gateway.%s.id
                }
                """, routeTableName, cidrBlock, internetGatewayName);

        // 2. Route Table에 라우트 추가 파일 이름 설정
        String routeTfFileName = String.format("route_%s_to_igw.tf", routeTableName);

        // 3. S3에 새로운 Route .tf 파일 업로드
        String s3Key = "users/" + userId + "/" + routeTfFileName;
        s3Service.uploadFileContent(s3Key, routeTfContent);

        // 4. Terraform 실행 요청
        terraformService.executeTerraform(userId);
    }
}