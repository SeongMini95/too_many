package com.ojeomme.controller;

import com.ojeomme.controller.support.AcceptanceTest;
import com.ojeomme.domain.eattogetherpost.EatTogetherPost;
import com.ojeomme.domain.eattogetherpost.repository.EatTogetherPostRepository;
import com.ojeomme.domain.eattogetherreply.EatTogetherReply;
import com.ojeomme.domain.eattogetherreply.repository.EatTogetherReplyRepository;
import com.ojeomme.domain.eattogetherreplyimage.EatTogetherReplyImage;
import com.ojeomme.domain.eattogetherreplyimage.repository.EatTogetherReplyImageRepository;
import com.ojeomme.domain.regioncode.repository.RegionCodeRepository;
import com.ojeomme.dto.request.eattogether.ModifyEatTogetherPostRequestDto;
import com.ojeomme.dto.request.eattogether.WriteEatTogetherPostRequestDto;
import com.ojeomme.dto.request.eattogether.WriteEatTogetherReplyRequestDto;
import com.ojeomme.exception.ApiErrorCode;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class EatTogetherControllerTest extends AcceptanceTest {

    @Autowired
    private EatTogetherPostRepository eatTogetherPostRepository;

    @Autowired
    private RegionCodeRepository regionCodeRepository;

    @Autowired
    private EatTogetherReplyRepository eatTogetherReplyRepository;

    @Autowired
    private EatTogetherReplyImageRepository eatTogetherReplyImageRepository;

    @Nested
    class modifyEatTogetherPost {

        private final ModifyEatTogetherPostRequestDto requestDto = ModifyEatTogetherPostRequestDto.builder()
                .subject("테스트 제목")
                .content("이미지1 <img src=\"http://localhost:4000/temp/2023/4/28/image1.png\"> 이미지2 <img src=\"http://localhost:4000/image2.png\"> 본문 내용")
                .build();

        @Test
        void 게시글을_수정한다() throws Exception {
            // given
            EatTogetherPost post = createPost();

            createImage("image1");

            // when
            ExtractableResponse<Response> response = RestAssured.given().log().all()
                    .auth().oauth2(accessToken)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(requestDto)
                    .when().put("/api/eatTogether/post/{postId}", post.getId())
                    .then().log().all()
                    .extract();

            // then
            assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        }

        @Test
        void 게시글이_없으면_EatTogetherPostNotFoundException를_발생한다() {
            // given

            // when
            ExtractableResponse<Response> response = RestAssured.given().log().all()
                    .auth().oauth2(accessToken)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(requestDto)
                    .when().put("/api/eatTogether/post/{postId}", -1L)
                    .then().log().all()
                    .extract();

            // then
            assertThat(response.statusCode()).isEqualTo(ApiErrorCode.EAT_TOGETHER_POST_NOT_FOUND.getHttpStatus().value());
            assertThat(response.asString()).isEqualTo(ApiErrorCode.EAT_TOGETHER_POST_NOT_FOUND.getMessage());
        }

        private EatTogetherPost createPost() {
            return eatTogetherPostRepository.save(EatTogetherPost.builder()
                    .user(user)
                    .regionCode(regionCodeRepository.findById("1111010100").orElseThrow())
                    .subject("테스트 제목")
                    .content("테스트 본문")
                    .build());
        }

        private void createImage(String filename) throws Exception {
            String uploadPath = "build/resources/test";

            BufferedImage bufferedImage = new BufferedImage(1000, 1000, BufferedImage.TYPE_INT_RGB);

            File tempFile = Paths.get(uploadPath, "/temp/2023/4/28/" + filename + ".png").toFile();
            Files.createDirectories(tempFile.getParentFile().toPath());

            FileOutputStream outputStream = new FileOutputStream(tempFile);
            ImageIO.write(bufferedImage, "png", outputStream);
            outputStream.close();
        }
    }

    @Nested
    class getEatTogetherReplyList {

        @Test
        void 댓글_리스트를_가져온다() {
            // given
            EatTogetherPost post = createPost();

            List<EatTogetherReply> replies = new ArrayList<>();
            List<EatTogetherReplyImage> images = new ArrayList<>();

            for (int i = 0; i < 10; i++) {
                Long seq = eatTogetherReplyRepository.nextval();
                EatTogetherReply reply = eatTogetherReplyRepository.save(EatTogetherReply.builder()
                        .id(seq)
                        .user(user)
                        .eatTogetherPost(post)
                        .upId(seq)
                        .content("댓글" + i)
                        .build());
                EatTogetherReplyImage image = eatTogetherReplyImageRepository.save(EatTogetherReplyImage.builder()
                        .eatTogetherReply(reply)
                        .imageUrl("image" + i)
                        .build());

                replies.add(reply);
                images.add(image);
            }

            // when
            ExtractableResponse<Response> response = RestAssured.given().log().all()
                    .auth().oauth2(accessToken)
                    .when().get("/api/eatTogether/post/{postId}/reply/list", post.getId())
                    .then().log().all()
                    .extract();

            JsonPath jsonPath = response.jsonPath();

            // then
            assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

            assertThat(jsonPath.getList("replies")).hasSameSizeAs(replies);
            for (int i = 0; i < jsonPath.getList("replies").size(); i++) {
                assertThat(jsonPath.getLong("replies[" + i + "].userId")).isEqualTo(replies.get(i).getUser().getId());
                assertThat(jsonPath.getString("replies[" + i + "].nickname")).isEqualTo(user.getNickname());
                assertThat(jsonPath.getString("replies[" + i + "].content")).isEqualTo(replies.get(i).getContent());
                assertThat(jsonPath.getString("replies[" + i + "].image")).isEqualTo(images.get(i).getImageUrl());
                assertThat(jsonPath.getString("replies[" + i + "].createDatetime")).isEqualTo(replies.get(i).getCreateDatetime().format(DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm")));
            }
        }

        @Test
        void 게시글이_없으면_EatTogetherPostNotFoundException를_발생한다() {
            // given

            // when
            ExtractableResponse<Response> response = RestAssured.given().log().all()
                    .auth().oauth2(accessToken)
                    .when().get("/api/eatTogether/post/{postId}/reply/list", -1L)
                    .then().log().all()
                    .extract();

            // then
            assertThat(response.statusCode()).isEqualTo(ApiErrorCode.EAT_TOGETHER_POST_NOT_FOUND.getHttpStatus().value());
            assertThat(response.asString()).isEqualTo(ApiErrorCode.EAT_TOGETHER_POST_NOT_FOUND.getMessage());
        }

        private EatTogetherPost createPost() {
            return eatTogetherPostRepository.save(EatTogetherPost.builder()
                    .user(user)
                    .regionCode(regionCodeRepository.findById("1111010100").orElseThrow())
                    .subject("테스트 제목")
                    .content("테스트 본문")
                    .build());
        }
    }

    @Nested
    class writeEatTogetherReply {

        @Test
        void 댓글을_작성한다() {
            // given
            EatTogetherPost post = createPost();

            WriteEatTogetherReplyRequestDto requestDto = WriteEatTogetherReplyRequestDto.builder()
                    .content("테스트 댓글")
                    .build();

            // when
            ExtractableResponse<Response> response = RestAssured.given().log().all()
                    .auth().oauth2(accessToken)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(requestDto)
                    .when().post("/api/eatTogether/post/{postId}/reply", post.getId())
                    .then().log().all()
                    .extract();

            // then
            assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        }

        @Test
        void 이미지가_존재하는_댓글을_작성한다() throws Exception {
            // given
            EatTogetherPost post = createPost();

            createImage("image1");

            WriteEatTogetherReplyRequestDto requestDto = WriteEatTogetherReplyRequestDto.builder()
                    .content("테스트 댓글")
                    .image("http://localhost:4000/temp/2023/4/27/image1.png")
                    .build();

            // when
            ExtractableResponse<Response> response = RestAssured.given().log().all()
                    .auth().oauth2(accessToken)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(requestDto)
                    .when().post("/api/eatTogether/post/{postId}/reply", post.getId())
                    .then().log().all()
                    .extract();

            // then
            assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        }

        @Test
        void 대댓글을_작성한다() {
            // given
            EatTogetherPost post = createPost();

            EatTogetherReply upReply = eatTogetherReplyRepository.save(EatTogetherReply.builder()
                    .id(-1L)
                    .user(user)
                    .eatTogetherPost(post)
                    .upId(-1L)
                    .content("댓글")
                    .build());

            WriteEatTogetherReplyRequestDto requestDto = WriteEatTogetherReplyRequestDto.builder()
                    .upReplyId(upReply.getId())
                    .content("테스트 댓글")
                    .build();

            // when
            ExtractableResponse<Response> response = RestAssured.given().log().all()
                    .auth().oauth2(accessToken)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(requestDto)
                    .when().post("/api/eatTogether/post/{postId}/reply", post.getId())
                    .then().log().all()
                    .extract();

            // then
            assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        }

        @Test
        void 대댓글을_작성하는데_대댓글이_존재하지_않으면_EatTogetherReplyNotFoundException를_발생한다() {
            // given
            EatTogetherPost post = createPost();

            WriteEatTogetherReplyRequestDto requestDto = WriteEatTogetherReplyRequestDto.builder()
                    .upReplyId(-1L)
                    .content("테스트 댓글")
                    .build();

            // when
            ExtractableResponse<Response> response = RestAssured.given().log().all()
                    .auth().oauth2(accessToken)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(requestDto)
                    .when().post("/api/eatTogether/post/{postId}/reply", post.getId())
                    .then().log().all()
                    .extract();

            // then
            assertThat(response.statusCode()).isEqualTo(ApiErrorCode.EAT_TOGETHER_REPLY_NOT_FOUND.getHttpStatus().value());
            assertThat(response.asString()).isEqualTo(ApiErrorCode.EAT_TOGETHER_REPLY_NOT_FOUND.getMessage());
        }

        @Test
        void 유저가_존재하지_않으면_UserNotFoundException를_발생한다() {
            // given
            EatTogetherPost post = createPost();

            WriteEatTogetherReplyRequestDto requestDto = WriteEatTogetherReplyRequestDto.builder()
                    .content("테스트 댓글")
                    .build();

            // when
            ExtractableResponse<Response> response = RestAssured.given().log().all()
                    .auth().oauth2(notExistAccessToken)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(requestDto)
                    .when().post("/api/eatTogether/post/{postId}/reply", post.getId())
                    .then().log().all()
                    .extract();

            // then
            assertThat(response.statusCode()).isEqualTo(ApiErrorCode.USER_NOT_FOUND.getHttpStatus().value());
            assertThat(response.asString()).isEqualTo(ApiErrorCode.USER_NOT_FOUND.getMessage());
        }

        @Test
        void 게시글이_존재하지_않으면_EatTogetherPostNotFound를_발생한다() {
            // given
            WriteEatTogetherReplyRequestDto requestDto = WriteEatTogetherReplyRequestDto.builder()
                    .content("테스트 댓글")
                    .build();

            // when
            ExtractableResponse<Response> response = RestAssured.given().log().all()
                    .auth().oauth2(accessToken)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(requestDto)
                    .when().post("/api/eatTogether/post/{postId}/reply", -1L)
                    .then().log().all()
                    .extract();

            // then
            assertThat(response.statusCode()).isEqualTo(ApiErrorCode.EAT_TOGETHER_POST_NOT_FOUND.getHttpStatus().value());
            assertThat(response.asString()).isEqualTo(ApiErrorCode.EAT_TOGETHER_POST_NOT_FOUND.getMessage());
        }

        private EatTogetherPost createPost() {
            return eatTogetherPostRepository.save(EatTogetherPost.builder()
                    .user(user)
                    .regionCode(regionCodeRepository.findById("1111010100").orElseThrow())
                    .subject("테스트 제목")
                    .content("테스트 본문")
                    .build());
        }

        private void createImage(String filename) throws Exception {
            String uploadPath = "build/resources/test";

            BufferedImage bufferedImage = new BufferedImage(1000, 1000, BufferedImage.TYPE_INT_RGB);

            File tempFile = Paths.get(uploadPath, "/temp/2023/4/27/" + filename + ".png").toFile();
            Files.createDirectories(tempFile.getParentFile().toPath());

            FileOutputStream outputStream = new FileOutputStream(tempFile);
            ImageIO.write(bufferedImage, "png", outputStream);
            outputStream.close();
        }
    }

    @Nested
    class getEatTogetherPostList {

        @Test
        void 게시글_목록을_가져온다() {
            // given
            List<EatTogetherPost> dummyData = eatTogetherPostRepository.saveAll(List.of(
                    EatTogetherPost.builder()
                            .user(user)
                            .regionCode(regionCodeRepository.findById("1111000000").orElseThrow())
                            .subject("테스트 제목")
                            .content("테스트 본문")
                            .build(),
                    EatTogetherPost.builder()
                            .user(user)
                            .regionCode(regionCodeRepository.findById("1111010100").orElseThrow())
                            .subject("테스트 제목")
                            .content("테스트 본문")
                            .build()
            ));

            // when
            ExtractableResponse<Response> response = RestAssured.given().log().all()
                    .auth().oauth2(accessToken)
                    .param("regionCode", "1111000000")
                    .when().get("/api/eatTogether/post/list")
                    .then().log().all()
                    .extract();

            JsonPath jsonPath = response.jsonPath();

            // then
            assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

            assertThat(jsonPath.getList("posts")).hasSameSizeAs(dummyData);
            for (int i = 0; i < jsonPath.getList("posts").size(); i++) {
                assertThat(jsonPath.getLong("posts[" + i + "].id")).isEqualTo(dummyData.get(dummyData.size() - 1 - i).getId());
                assertThat(jsonPath.getString("posts[" + i + "].nickname")).isEqualTo(dummyData.get(dummyData.size() - 1 - i).getUser().getNickname());
                assertThat(jsonPath.getString("posts[" + i + "].regionName")).isEqualTo(dummyData.get(dummyData.size() - 1 - i).getRegionCode().getRegionName());
                assertThat(jsonPath.getString("posts[" + i + "].subject")).isEqualTo(dummyData.get(dummyData.size() - 1 - i).getSubject());
            }
        }

        @Test
        void 다음_페이지_게시글_목록을_가져온다() {
            // given
            List<EatTogetherPost> posts = new ArrayList<>();
            for (int i = 0; i < 40; i++) {
                posts.add(EatTogetherPost.builder()
                        .user(user)
                        .regionCode(regionCodeRepository.findById("1111000000").orElseThrow())
                        .subject("테스트 제목" + i)
                        .content("테스트 본문" + i)
                        .build());
            }
            posts = eatTogetherPostRepository.saveAll(posts);
            posts = posts.stream()
                    .sorted(Comparator.comparing(EatTogetherPost::getId).reversed())
                    .collect(Collectors.toList())
                    .subList(30, 40);

            // when
            ExtractableResponse<Response> response = RestAssured.given().log().all()
                    .auth().oauth2(accessToken)
                    .param("regionCode", "1111000000")
                    .param("moreId", 11)
                    .when().get("/api/eatTogether/post/list")
                    .then().log().all()
                    .extract();

            JsonPath jsonPath = response.jsonPath();

            // then
            assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

            assertThat(jsonPath.getList("posts")).hasSameSizeAs(posts);
            for (int i = 0; i < jsonPath.getList("posts").size(); i++) {
                assertThat(jsonPath.getLong("posts[" + i + "].id")).isEqualTo(posts.get(i).getId());
                assertThat(jsonPath.getString("posts[" + i + "].nickname")).isEqualTo(posts.get(i).getUser().getNickname());
                assertThat(jsonPath.getString("posts[" + i + "].regionName")).isEqualTo(posts.get(i).getRegionCode().getRegionName());
                assertThat(jsonPath.getString("posts[" + i + "].subject")).isEqualTo(posts.get(i).getSubject());
            }
        }
    }

    @Nested
    class getEatTogetherPost {

        @Test
        void 게시글을_가져온다() {
            // given
            EatTogetherPost eatTogetherPost = eatTogetherPostRepository.save(EatTogetherPost.builder()
                    .user(user)
                    .regionCode(regionCodeRepository.findById("1111010100").orElseThrow())
                    .subject("테스트 제목")
                    .content("테스트 본문")
                    .build());

            // when
            ExtractableResponse<Response> response = RestAssured.given().log().all()
                    .auth().oauth2(accessToken)
                    .when().get("/api/eatTogether/post/{postId}", eatTogetherPost.getId())
                    .then().log().all()
                    .extract();

            JsonPath jsonPath = response.jsonPath();

            // then
            assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

            assertThat(jsonPath.getLong("id")).isEqualTo(eatTogetherPost.getId());
            assertThat(jsonPath.getLong("userId")).isEqualTo(eatTogetherPost.getUser().getId());
            assertThat(jsonPath.getString("nickname")).isEqualTo(eatTogetherPost.getUser().getNickname());
            assertThat(jsonPath.getString("regionCode")).isEqualTo(eatTogetherPost.getRegionCode().getCode());
            assertThat(jsonPath.getString("regionName")).isEqualTo(eatTogetherPost.getRegionCode().getRegionName());
            assertThat(jsonPath.getString("subject")).isEqualTo(eatTogetherPost.getSubject());
            assertThat(jsonPath.getString("content")).isEqualTo(eatTogetherPost.getContent());
            assertThat(jsonPath.getString("createDatetime")).isEqualTo(eatTogetherPost.getCreateDatetime().format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 HH:mm:ss")));
        }

        @Test
        void 게시물을_못찾으면_EatTogetherPostNotFound를_발생한다() {
            // given

            // when
            ExtractableResponse<Response> response = RestAssured.given().log().all()
                    .auth().oauth2(accessToken)
                    .when().get("/api/eatTogether/post/{postId}", -1L)
                    .then().log().all()
                    .extract();

            // then
            assertThat(response.statusCode()).isEqualTo(ApiErrorCode.EAT_TOGETHER_POST_NOT_FOUND.getHttpStatus().value());
            assertThat(response.asString()).isEqualTo(ApiErrorCode.EAT_TOGETHER_POST_NOT_FOUND.getMessage());
        }
    }

    @Nested
    class writeEatTogetherPost {

        private final WriteEatTogetherPostRequestDto requestDto = WriteEatTogetherPostRequestDto.builder()
                .regionCode("1111012200")
                .subject("게시글 제목")
                .content("이미지1 <img src=\"http://localhost:4000/temp/2023/4/25/image1.png\"> 이미지2 http://localhost:4000/temp/2023/4/25/image1.png 본문 내용")
                .build();

        @Test
        void 게시판에_글을_작성한다() throws Exception {
            // given
            createImage("image1");

            // when
            ExtractableResponse<Response> response = RestAssured.given().log().all()
                    .auth().oauth2(accessToken)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(requestDto)
                    .when().post("/api/eatTogether/post")
                    .then().log().all()
                    .extract();

            // then
            assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        }

        @Test
        void 유저가_없으면_UserNotFoundException를_발생한다() {
            // given

            // when
            ExtractableResponse<Response> response = RestAssured.given().log().all()
                    .auth().oauth2(notExistAccessToken)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(requestDto)
                    .when().post("/api/eatTogether/post")
                    .then().log().all()
                    .extract();

            // then
            assertThat(response.statusCode()).isEqualTo(ApiErrorCode.USER_NOT_FOUND.getHttpStatus().value());
            assertThat(response.asString()).isEqualTo(ApiErrorCode.USER_NOT_FOUND.getMessage());
        }

        @Test
        void 지역코드가_없으면_RegionCodeNotFoundException를_발생한다() {
            // given
            WriteEatTogetherPostRequestDto requestDto = WriteEatTogetherPostRequestDto.builder()
                    .regionCode("-1")
                    .subject("게시글 제목")
                    .content("이미지1 <img src=\"http://localhost:4000/temp/2023/4/25/image1.png\"> 이미지2 http://localhost:4000/temp/2023/4/25/image1.png 본문 내용")
                    .build();

            // when
            ExtractableResponse<Response> response = RestAssured.given().log().all()
                    .auth().oauth2(accessToken)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(requestDto)
                    .when().post("/api/eatTogether/post")
                    .then().log().all()
                    .extract();

            // then
            assertThat(response.statusCode()).isEqualTo(ApiErrorCode.REGION_CODE_NOT_FOUND.getHttpStatus().value());
            assertThat(response.asString()).isEqualTo(ApiErrorCode.REGION_CODE_NOT_FOUND.getMessage());
        }

        private void createImage(String filename) throws Exception {
            String uploadPath = "build/resources/test";

            BufferedImage bufferedImage = new BufferedImage(1000, 1000, BufferedImage.TYPE_INT_RGB);

            File tempFile = Paths.get(uploadPath, "/temp/2023/4/25/" + filename + ".png").toFile();
            Files.createDirectories(tempFile.getParentFile().toPath());

            FileOutputStream outputStream = new FileOutputStream(tempFile);
            ImageIO.write(bufferedImage, "png", outputStream);
            outputStream.close();
        }
    }
}