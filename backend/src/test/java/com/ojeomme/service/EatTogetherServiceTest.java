package com.ojeomme.service;

import com.ojeomme.domain.eattogetherpost.EatTogetherPost;
import com.ojeomme.domain.eattogetherpost.repository.EatTogetherPostRepository;
import com.ojeomme.domain.eattogetherreply.EatTogetherReply;
import com.ojeomme.domain.eattogetherreply.repository.EatTogetherReplyRepository;
import com.ojeomme.domain.eattogetherreplyimage.EatTogetherReplyImage;
import com.ojeomme.domain.eattogetherreplyimage.repository.EatTogetherReplyImageRepository;
import com.ojeomme.domain.regioncode.RegionCode;
import com.ojeomme.domain.regioncode.repository.RegionCodeRepository;
import com.ojeomme.domain.user.User;
import com.ojeomme.domain.user.repository.UserRepository;
import com.ojeomme.dto.request.eattogether.ModifyEatTogetherPostRequestDto;
import com.ojeomme.dto.request.eattogether.WriteEatTogetherPostRequestDto;
import com.ojeomme.dto.request.eattogether.WriteEatTogetherReplyRequestDto;
import com.ojeomme.dto.response.eattogether.EatTogetherPostListResponseDto;
import com.ojeomme.dto.response.eattogether.EatTogetherPostResponseDto;
import com.ojeomme.dto.response.eattogether.EatTogetherReplyListResponseDto;
import com.ojeomme.exception.ApiErrorCode;
import com.ojeomme.exception.ApiException;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class EatTogetherServiceTest {

    @InjectMocks
    private EatTogetherService eatTogetherService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RegionCodeRepository regionCodeRepository;

    @Mock
    private EatTogetherPostRepository eatTogetherPostRepository;

    @Mock
    private EatTogetherReplyRepository eatTogetherReplyRepository;

    @Mock
    private EatTogetherReplyImageRepository eatTogetherReplyImageRepository;

    @Mock
    private ImageService imageService;

    @Nested
    class deleteEatTogetherPost {

        @Test
        void 게시글을_삭제한다() {
            // given
            given(eatTogetherPostRepository.findByIdAndUserId(anyLong(), anyLong())).willReturn(Optional.of(mock(EatTogetherPost.class)));

            // when
            eatTogetherService.deleteEatTogetherPost(1L, 1L);

            // then
            then(eatTogetherPostRepository).should(times(1)).delete(any(EatTogetherPost.class));
        }

        @Test
        void 게시글이_없으면_EatTogetherPostNotFoundException를_발생한다() {
            // given
            given(eatTogetherPostRepository.findByIdAndUserId(anyLong(), anyLong())).willReturn(Optional.empty());

            // when
            ApiException exception = assertThrows(ApiException.class, () -> eatTogetherService.deleteEatTogetherPost(1L, 1L));

            // then
            assertThat(exception.getErrorCode()).isEqualTo(ApiErrorCode.EAT_TOGETHER_POST_NOT_FOUND);
        }
    }

    @Nested
    class modifyEatTogetherPost {

        private final ModifyEatTogetherPostRequestDto requestDto = ModifyEatTogetherPostRequestDto.builder()
                .subject("테스트 제목")
                .content("이미지1 <img src=\"http://localhost:4000/temp/image1.png\"> 이미지2 <img src=\"http://localhost:4000/image2.png\"> 본문 내용")
                .build();

        @Test
        void 게시글을_수정한다() throws IOException {
            // given
            ReflectionTestUtils.setField(eatTogetherService, "host", "http://localhost:4000");

            EatTogetherPost eatTogetherPost = mock(EatTogetherPost.class);
            given(eatTogetherPostRepository.findByIdAndUserId(anyLong(), anyLong())).willReturn(Optional.of(eatTogetherPost));
            given(imageService.copyImage(eq("http://localhost:4000/temp/image1.png"))).willReturn("http://localhost:4000/image1.png");
            given(imageService.copyImage(eq("http://localhost:4000/image2.png"))).willReturn("http://localhost:4000/image2.png");

            // when
            eatTogetherService.modifyEatTogetherPost(1L, 1L, requestDto);

            // then
            then(eatTogetherPost).should(times(1)).modifyPost(any(EatTogetherPost.class));
        }

        @Test
        void 게시글이_없으면_EatTogetherPostNotFoundException를_발생한다() {
            // given
            given(eatTogetherPostRepository.findByIdAndUserId(anyLong(), anyLong())).willReturn(Optional.empty());

            // when
            ApiException exception = assertThrows(ApiException.class, () -> eatTogetherService.modifyEatTogetherPost(1L, 1L, requestDto));

            // then
            assertThat(exception.getErrorCode()).isEqualTo(ApiErrorCode.EAT_TOGETHER_POST_NOT_FOUND);
        }
    }

    @Nested
    class getReplyList {

        @Test
        void 댓글_리스트를_가져온다() {
            // given
            EatTogetherReplyListResponseDto dto = new EatTogetherReplyListResponseDto(List.of(
                    EatTogetherReplyListResponseDto.ReplyResponseDto.builder()
                            .userId(1L)
                            .nickname("test1")
                            .content("댓글1")
                            .image("image1")
                            .createDatetime(LocalDateTime.now())
                            .build(),
                    EatTogetherReplyListResponseDto.ReplyResponseDto.builder()
                            .userId(1L)
                            .nickname("test2")
                            .upNickname("test1")
                            .content("댓글2")
                            .createDatetime(LocalDateTime.now())
                            .build()
            ));

            given(eatTogetherPostRepository.findById(anyLong())).willReturn(Optional.of(mock(EatTogetherPost.class)));
            given(eatTogetherReplyRepository.getReplyList(anyLong())).willReturn(dto);

            // when
            EatTogetherReplyListResponseDto responseDto = eatTogetherService.getEatTogetherReplyList(1L);

            // then
            assertThat(responseDto.getReplies()).hasSameSizeAs(dto.getReplies());
            for (int i = 0; i < responseDto.getReplies().size(); i++) {
                assertThat(responseDto.getReplies().get(i).getUserId()).isEqualTo(dto.getReplies().get(i).getUserId());
                assertThat(responseDto.getReplies().get(i).getNickname()).isEqualTo(dto.getReplies().get(i).getNickname());
                assertThat(responseDto.getReplies().get(i).getUpNickname()).isEqualTo(dto.getReplies().get(i).getUpNickname());
                assertThat(responseDto.getReplies().get(i).getContent()).isEqualTo(dto.getReplies().get(i).getContent());
                assertThat(responseDto.getReplies().get(i).getImage()).isEqualTo(dto.getReplies().get(i).getImage());
                assertThat(responseDto.getReplies().get(i).getCreateDatetime()).isEqualTo(dto.getReplies().get(i).getCreateDatetime());
            }
        }

        @Test
        void 게시글이_없으면_EatTogetherPostNotFoundException를_발생한다() {
            // given
            given(eatTogetherPostRepository.findById(anyLong())).willReturn(Optional.empty());

            // when
            ApiException exception = assertThrows(ApiException.class, () -> eatTogetherService.getEatTogetherReplyList(1L));

            // then
            assertThat(exception.getErrorCode()).isEqualTo(ApiErrorCode.EAT_TOGETHER_POST_NOT_FOUND);
        }
    }

    @Nested
    class writeEatTogetherReply {

        @Test
        void 댓글을_작성한다() throws IOException {
            // given
            given(userRepository.findById(anyLong())).willReturn(Optional.of(mock(User.class)));
            given(eatTogetherPostRepository.findById(anyLong())).willReturn(Optional.of(mock(EatTogetherPost.class)));

            given(eatTogetherReplyRepository.nextval()).willReturn(1L);
            given(eatTogetherReplyRepository.save(any(EatTogetherReply.class))).willReturn(mock(EatTogetherReply.class));

            WriteEatTogetherReplyRequestDto requestDto = WriteEatTogetherReplyRequestDto.builder()
                    .content("테스트 댓글")
                    .build();

            // when
            eatTogetherService.writeEatTogetherReply(1L, 1L, requestDto);

            // then
            then(eatTogetherReplyRepository).should(times(1)).save(any(EatTogetherReply.class));
        }

        @Test
        void 이미지가_존재하는_댓글을_작성한다() throws IOException {
            // given
            given(userRepository.findById(anyLong())).willReturn(Optional.of(mock(User.class)));
            given(eatTogetherPostRepository.findById(anyLong())).willReturn(Optional.of(mock(EatTogetherPost.class)));

            given(eatTogetherReplyRepository.nextval()).willReturn(1L);
            given(eatTogetherReplyRepository.save(any(EatTogetherReply.class))).willReturn(mock(EatTogetherReply.class));

            given(eatTogetherReplyImageRepository.save(any(EatTogetherReplyImage.class))).willReturn(mock(EatTogetherReplyImage.class));

            WriteEatTogetherReplyRequestDto requestDto = WriteEatTogetherReplyRequestDto.builder()
                    .content("테스트 댓글")
                    .image("image1")
                    .build();

            // when
            eatTogetherService.writeEatTogetherReply(1L, 1L, requestDto);

            // then
            then(eatTogetherReplyRepository).should(times(1)).save(any(EatTogetherReply.class));
        }

        @Test
        void 대댓글을_작성한다() throws IOException {
            // given
            given(userRepository.findById(anyLong())).willReturn(Optional.of(mock(User.class)));
            given(eatTogetherPostRepository.findById(anyLong())).willReturn(Optional.of(mock(EatTogetherPost.class)));

            given(eatTogetherReplyRepository.findById(anyLong())).willReturn(Optional.of(mock(EatTogetherReply.class)));

            given(eatTogetherReplyRepository.nextval()).willReturn(2L);
            given(eatTogetherReplyRepository.save(any(EatTogetherReply.class))).willReturn(mock(EatTogetherReply.class));


            WriteEatTogetherReplyRequestDto requestDto = WriteEatTogetherReplyRequestDto.builder()
                    .upReplyId(1L)
                    .content("테스트 댓글")
                    .build();

            // when
            eatTogetherService.writeEatTogetherReply(1L, 1L, requestDto);

            // then
            then(eatTogetherReplyRepository).should(times(1)).save(any(EatTogetherReply.class));
        }

        @Test
        void 대댓글을_작성하는데_대댓글이_존재하지_않으면_EatTogetherReplyNotFoundException를_발생한다() {
            // given
            given(userRepository.findById(anyLong())).willReturn(Optional.of(mock(User.class)));
            given(eatTogetherPostRepository.findById(anyLong())).willReturn(Optional.of(mock(EatTogetherPost.class)));

            given(eatTogetherReplyRepository.findById(anyLong())).willReturn(Optional.empty());

            WriteEatTogetherReplyRequestDto requestDto = WriteEatTogetherReplyRequestDto.builder()
                    .upReplyId(1L)
                    .content("테스트 댓글")
                    .build();

            // when
            ApiException exception = assertThrows(ApiException.class, () -> eatTogetherService.writeEatTogetherReply(1L, 1L, requestDto));

            // then
            assertThat(exception.getErrorCode()).isEqualTo(ApiErrorCode.EAT_TOGETHER_REPLY_NOT_FOUND);
        }

        @Test
        void 유저가_존재하지_않으면_UserNotFoundException를_발생한다() {
            // given
            given(userRepository.findById(anyLong())).willReturn(Optional.empty());

            WriteEatTogetherReplyRequestDto requestDto = WriteEatTogetherReplyRequestDto.builder()
                    .content("테스트 댓글")
                    .build();

            // when
            ApiException exception = assertThrows(ApiException.class, () -> eatTogetherService.writeEatTogetherReply(1L, 1L, requestDto));

            // then
            assertThat(exception.getErrorCode()).isEqualTo(ApiErrorCode.USER_NOT_FOUND);
        }

        @Test
        void 게시글이_존재하지_않으면_EatTogetherPostNotFound를_발생한다() {
            // given
            given(userRepository.findById(anyLong())).willReturn(Optional.of(mock(User.class)));
            given(eatTogetherPostRepository.findById(anyLong())).willReturn(Optional.empty());

            WriteEatTogetherReplyRequestDto requestDto = WriteEatTogetherReplyRequestDto.builder()
                    .content("테스트 댓글")
                    .build();

            // when
            ApiException exception = assertThrows(ApiException.class, () -> eatTogetherService.writeEatTogetherReply(1L, 1L, requestDto));

            // then
            assertThat(exception.getErrorCode()).isEqualTo(ApiErrorCode.EAT_TOGETHER_POST_NOT_FOUND);
        }
    }

    @Nested
    class getEatTogetherPostList {

        @Test
        void 게시글_목록을_가져온다() {
            // given
            List<EatTogetherPostListResponseDto.PostResponseDto> posts = List.of(
                    EatTogetherPostListResponseDto.PostResponseDto.builder()
                            .id(1L)
                            .nickname("test123")
                            .regionName("청진동")
                            .subject("제목1")
                            .oriCreateDatetime(LocalDateTime.now())
                            .build(),
                    EatTogetherPostListResponseDto.PostResponseDto.builder()
                            .id(1L)
                            .nickname("test123")
                            .regionName("청진동")
                            .subject("제목2")
                            .oriCreateDatetime(LocalDateTime.now().minusDays(1))
                            .build()
            );
            EatTogetherPostListResponseDto dto = new EatTogetherPostListResponseDto(posts);

            given(eatTogetherPostRepository.getEatTogetherPostList(anyString(), anyLong())).willReturn(dto);

            // when
            EatTogetherPostListResponseDto responseDto = eatTogetherService.getEatTogetherPostList("123", 1L);

            // then
            assertThat(responseDto.getPosts()).hasSameSizeAs(dto.getPosts());
            for (int i = 0; i < responseDto.getPosts().size(); i++) {
                assertThat(responseDto.getPosts().get(i).getId()).isEqualTo(dto.getPosts().get(i).getId());
                assertThat(responseDto.getPosts().get(i).getNickname()).isEqualTo(dto.getPosts().get(i).getNickname());
                assertThat(responseDto.getPosts().get(i).getRegionName()).isEqualTo(dto.getPosts().get(i).getRegionName());
                assertThat(responseDto.getPosts().get(i).getSubject()).isEqualTo(dto.getPosts().get(i).getSubject());
                assertThat(responseDto.getPosts().get(i).getCreateDatetime()).isEqualTo(dto.getPosts().get(i).getCreateDatetime());
            }
        }
    }

    @Nested
    class getEatTogetherPost {

        @Test
        void 게시글을_가져온다() {
            // given
            EatTogetherPostResponseDto dto = EatTogetherPostResponseDto.builder()
                    .id(1L)
                    .userId(1L)
                    .nickname("test123")
                    .regionCode("12345")
                    .regionName("청진동")
                    .subject("제목")
                    .content("본문 내용")
                    .build();
            given(eatTogetherPostRepository.getEatTogetherPost(anyLong())).willReturn(Optional.of(dto));

            // when
            EatTogetherPostResponseDto responseDto = eatTogetherService.getEatTogetherPost(1L);

            // then
            assertThat(responseDto.getId()).isEqualTo(dto.getId());
            assertThat(responseDto.getUserId()).isEqualTo(dto.getUserId());
            assertThat(responseDto.getNickname()).isEqualTo(dto.getNickname());
            assertThat(responseDto.getRegionCode()).isEqualTo(dto.getRegionCode());
            assertThat(responseDto.getRegionName()).isEqualTo(dto.getRegionName());
            assertThat(responseDto.getSubject()).isEqualTo(dto.getSubject());
            assertThat(responseDto.getContent()).isEqualTo(dto.getContent());
        }

        @Test
        void 게시물을_못찾으면_EatTogetherPostNotFound를_발생한다() {
            // given
            given(eatTogetherPostRepository.getEatTogetherPost(anyLong())).willReturn(Optional.empty());

            // when
            ApiException exception = assertThrows(ApiException.class, () -> eatTogetherService.getEatTogetherPost(1L));

            // then
            assertThat(exception.getErrorCode()).isEqualTo(ApiErrorCode.EAT_TOGETHER_POST_NOT_FOUND);
        }
    }

    @Nested
    class writeEatTogetherPost {

        private final WriteEatTogetherPostRequestDto requestDto = WriteEatTogetherPostRequestDto.builder()
                .regionCode("1234")
                .subject("테스트 제목")
                .content("이미지1 <img src=\"http://localhost:4000/temp/image1.png\"> 이미지2 http://localhost:4000/temp/image2.png 본문 내용")
                .build();

        @Test
        void 게시판에_글을_작성한다() throws IOException {
            // given
            ReflectionTestUtils.setField(eatTogetherService, "host", "http://localhost:4000");

            given(userRepository.findById(anyLong())).willReturn(Optional.of(mock(User.class)));
            given(regionCodeRepository.findById(anyString())).willReturn(Optional.of(mock(RegionCode.class)));
            given(imageService.copyImage(eq("http://localhost:4000/temp/image1.png"))).willReturn("http://localhost:4000/image1.png");
            given(eatTogetherPostRepository.save(any(EatTogetherPost.class))).willReturn(mock(EatTogetherPost.class));

            // when
            Long postId = eatTogetherService.writeEatTogetherPost(1L, requestDto);

            // then
            assertThat(postId).isNotNull();
        }

        @Test
        void 유저가_없으면_UserNotFoundException를_발생한다() {
            // given
            given(userRepository.findById(anyLong())).willReturn(Optional.empty());

            // when
            ApiException exception = assertThrows(ApiException.class, () -> eatTogetherService.writeEatTogetherPost(1L, requestDto));

            // then
            assertThat(exception.getErrorCode()).isEqualTo(ApiErrorCode.USER_NOT_FOUND);
        }

        @Test
        void 지역코드가_없으면_RegionCodeNotFoundException를_발생한다() {
            // given
            given(userRepository.findById(anyLong())).willReturn(Optional.of(mock(User.class)));
            given(regionCodeRepository.findById(anyString())).willReturn(Optional.empty());

            // when
            ApiException exception = assertThrows(ApiException.class, () -> eatTogetherService.writeEatTogetherPost(1L, requestDto));

            // then
            assertThat(exception.getErrorCode()).isEqualTo(ApiErrorCode.REGION_CODE_NOT_FOUND);
        }
    }
}