package AIWA.McpBackend.service.aws.vpc;

import AIWA.McpBackend.provider.aws.api.dto.vpc.VpcRequestDto;
import AIWA.McpBackend.service.aws.s3.S3Service;
import AIWA.McpBackend.service.terraform.TerraformService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VpcService {

    private final S3Service s3Service;

    private final TerraformService terraformService;
    /**
     * VPC를 생성합니다.
     *
     * @param vpcRequest VPC 생성 요청 DTO
     * @param userId     사용자 ID
     * @throws Exception VPC 생성 중 발생한 예외
     */

    public void createVpc(VpcRequestDto vpcRequest, String userId) throws Exception {
        // 1. 새로운 VPC .tf 파일 생성
        String vpcTfContent = String.format("""
                resource "aws_vpc" "%s" {
                  cidr_block = "%s"
                  tags = {
                    Name = "%s"
                  }
                }
                """, vpcRequest.getVpcName(), vpcRequest.getCidrBlock(), vpcRequest.getVpcName());

        // 2. VPC .tf 파일 이름 설정 (예: vpc_myVPC.tf)
        String vpcTfFileName = String.format("vpc_%s.tf", vpcRequest.getVpcName());

        // 3. 콘솔에 내용 출력 (디버깅 용도)
        System.out.println(vpcTfContent);

        // 4. S3에 새로운 VPC .tf 파일 업로드
        String s3Key = "users/" + userId + "/" + vpcTfFileName;
        s3Service.uploadFileContent(s3Key, vpcTfContent);

        // 5. Terraform 실행 요청
        terraformService.executeTerraform(userId);
    }

    /**
     * VPC를 삭제합니다.
     *
     * @param vpcName VPC 이름
     * @param userId  사용자 ID
     * @throws Exception VPC 삭제 중 발생한 예외
     */
    public void deleteVpc(String vpcName, String userId) throws Exception {
        // 1. 삭제하려는 VPC .tf 파일 이름 설정 (예: vpc_myVPC.tf)
        String vpcTfFileName = String.format("vpc_%s.tf", vpcName);

        // 2. S3에서 해당 VPC .tf 파일 삭제
        String s3Key = "users/" + userId + "/" + vpcTfFileName;
        s3Service.deleteFile(s3Key);

        // 3. Terraform 실행 요청
        terraformService.executeTerraform(userId);
    }

    /**
     * 사용자가 생성한 VPC 목록을 가져옵니다.
     *
     * @param userId 사용자 ID
     * @return 사용자가 생성한 VPC 목록
     */
    public List<String> getUserVpcList(String userId) {
        String userPrefix = "users/" + userId + "/";
        List<String> vpcFiles = s3Service.listFiles(userPrefix, "vpc_");

        // 파일 경로에서 마지막 슬래시 이후의 파일 이름만 추출하여 반환
        return vpcFiles.stream()
                .map(filename -> filename.substring(filename.lastIndexOf('/') + 1)) // 경로 제거하고 파일 이름만 추출
                .map(filename -> filename.replace("vpc_", "").replace(".tf", ""))  // vpc_ 접두사와 .tf 확장자 제거
                .collect(Collectors.toList());
    }
}
