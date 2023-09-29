package Farm.Team4.findOwn.service.board.post;

import Farm.Team4.findOwn.domain.board.Tag;
import Farm.Team4.findOwn.domain.board.post.Post;
import Farm.Team4.findOwn.domain.board.post.PostWithTag;
import Farm.Team4.findOwn.dto.board.comment.response.CommentDTO;
import Farm.Team4.findOwn.dto.board.post.request.SavePostRequest;
import Farm.Team4.findOwn.dto.board.post.request.UpdatePostRequest;
import Farm.Team4.findOwn.dto.board.post.response.DetailPostDTO;
import Farm.Team4.findOwn.dto.board.post.response.SavePostResponse;
import Farm.Team4.findOwn.dto.board.post.response.SimplePostDTO;
import Farm.Team4.findOwn.dto.board.post.response.UpdatePostResponse;
import Farm.Team4.findOwn.exception.CustomErrorCode;
import Farm.Team4.findOwn.exception.FindOwnException;
import Farm.Team4.findOwn.repository.board.PostRepository;
import Farm.Team4.findOwn.service.board.TagService;
import Farm.Team4.findOwn.service.member.information.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {
    private final PostRepository postRepository;
    private final MemberService memberService;
    private final TagService tagService;
    private final PostWithTagService postWithTagService;
    @Transactional
    public SavePostResponse savePost(SavePostRequest request){
        Post savedPost = postRepository.save(request.changeToPost(memberService.findById(request.getWriterId())));
        log.info("게시글 저장 완료");
        tagService.saveNewTag(request.getTagNames()); // 새로운 태그 저장 & 기존 태그는 저장은 X
        postWithTagService.saveAssociations(request.getTagNames(), savedPost);

        return new SavePostResponse(savedPost.getId(), savedPost.getTitle(), savedPost.getContent(), savedPost.getCreatedAt());
    }
    public Post findById(Long postId){
        return postRepository.findById(postId)
                .orElseThrow(() -> new FindOwnException(CustomErrorCode.NOT_MATCH_POST));
    }
    public Long countPosts(){
        return postRepository.count();
    }
    public List<SimplePostDTO> startPagingBoard(PageRequest pageRequest){
        return postRepository.findAll(pageRequest).stream()
                .map(post -> new SimplePostDTO(
                        post.getId(),
                        post.getMember().getNickname(),
                        post.getTitle(),
                        post.getTags().stream()
                                .map(association -> association.getTag().getName())
                                .toList(),
                        post.getCreatedAt()
                )).toList();
    }
    public DetailPostDTO findDetailPost(Long postId){
        Post findPost = findById(postId);
        return new DetailPostDTO(
                findPost.getMember().getNickname(),
                findPost.getTitle(),
                findPost.getContent(),
                findPost.getCreatedAt(),
                findPost.getTags().stream()
                        .map(association -> association.getTag().getName())
                        .toList(),
                findPost.getComments().stream()
                        .map(comment -> new CommentDTO(comment.getId(), comment.getWriter().getNickname(), comment.getContent(), comment.getCreatedAt()))
                        .toList()
        );
    }
    @Transactional
    public Post updatePost(UpdatePostRequest request) {
        Post findPost = memberService.findById(request.getWriterId()).getMyPosts().stream()
                .filter(myPost -> myPost.getId().equals(request.getPostId()))
                .findFirst()
                .orElseThrow(() -> new FindOwnException(CustomErrorCode.NOT_MATCH_POST));
        log.info("게시글 조회 성공");
        return findPost.updatePost(request);
    }
}