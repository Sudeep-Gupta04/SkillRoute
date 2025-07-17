package com.example.SkillRoute.service.Blog;

import com.cloudinary.Cloudinary;
import com.example.SkillRoute.dto.Blog.PostDetails.CommentSummary;
import com.example.SkillRoute.dto.Blog.PostDetails.PostDetailResponse;
import com.example.SkillRoute.dto.Blog.PostDetails.UserSummary;
import com.example.SkillRoute.dto.CreatePost.CreatePostRequest;
import com.example.SkillRoute.dto.CreatePost.PostResponse;
import com.example.SkillRoute.model.Blog.Post;
import com.example.SkillRoute.model.Blog.VoteType;
import com.example.SkillRoute.model.Roadmaps.Roadmap;
import com.example.SkillRoute.model.Roadmaps.Topic;
import com.example.SkillRoute.model.User;
import com.example.SkillRoute.repository.BlogRepo.PostRepository;
import com.example.SkillRoute.repository.BlogRepo.PostVoteRepository;
import com.example.SkillRoute.repository.RoadmapsRepo.RoadmapRepository;
import com.example.SkillRoute.repository.RoadmapsRepo.TopicRepository;
import com.example.SkillRoute.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class PostService {
    @Autowired
    Cloudinary cloudinary;

    @Autowired
    PostRepository postRepo;
    @Autowired
    PostVoteRepository postvoteRepo;

    @Autowired
    UserRepository userRepo;

    @Autowired
    RoadmapRepository roadmapRepo;

    @Autowired
    TopicRepository topicRepo;

    public PostDetailResponse getPostDetails(Long postId) {
        Post post = postRepo.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        int upvotes = (int) post.getPostVotes().stream().filter(v -> v.getVoteType() == VoteType.UPVOTE).count();
        int downvotes = (int) post.getPostVotes().stream().filter(v -> v.getVoteType() == VoteType.DOWNVOTE).count();

        List<CommentSummary> topLevelComments = post.getComments().stream()
                .filter(c -> c.getParent() == null)
                .map(comment -> {
                    int commentUpvotes = (int) comment.getCommentVotes().stream().filter(v -> v.getVoteType() == VoteType.UPVOTE).count();
                    int commentDownvotes = (int) comment.getCommentVotes().stream().filter(v -> v.getVoteType() == VoteType.DOWNVOTE).count();
                    int repliesCount = comment.getReplies() != null ? comment.getReplies().size() : 0;

                    return new CommentSummary(
                            comment.getId(),
                            comment.getText(),
                            comment.getCreatedAt(),
                            new UserSummary(comment.getUser().getId(), comment.getUser().getUsername()),
                            commentUpvotes,
                            commentDownvotes,
                            repliesCount
                    );
                })
                .toList();

        return new PostDetailResponse(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                post.getCreatedAt(),
                new UserSummary(post.getUser().getId(), post.getUser().getUsername()),
                upvotes,
                downvotes,
                topLevelComments
        );
    }


    @Transactional
    public PostResponse createPost(Long userId, CreatePostRequest request) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Roadmap roadmap = null;
        if (request.getRoadmapId() != null) {
            roadmap = roadmapRepo.findById(request.getRoadmapId())
                    .orElseThrow(() -> new RuntimeException("Roadmap not found"));
        }

        Topic topic = null;
        if (request.getTopicId() != null) {
            topic = topicRepo.findById(request.getTopicId())
                    .orElseThrow(() -> new RuntimeException("Topic not found"));
        }

        Post post = Post.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .user(user)
                .roadmap(roadmap)
                .topic(topic)
                .createdAt(LocalDateTime.now())
                .build();

        post = postRepo.save(post);

        return PostResponse.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .createdAt(String.valueOf(post.getCreatedAt()))
                .user(PostResponse.UserInfo.builder()
                        .id(user.getId())
                        .username(user.getUsername())
                        .build())
                .upvotes(0)
                .downvotes(0)
                .comments(new ArrayList<>())
                .build();
    }

    public List<PostResponse> getAllOrSearchPosts(String search) {
        List<Post> posts;

        if (search != null && !search.trim().isEmpty()) {
            posts = postRepo.searchByTitle(search.trim());
        } else {
            posts = postRepo.findAll();
        }

        return posts.stream()
                .map(this::mapToPostResponse)
                .toList();
    }
    private PostResponse mapToPostResponse(Post post) {
        return PostResponse.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .createdAt(post.getCreatedAt().toString())
                .user(PostResponse.UserInfo.builder()
                        .id(post.getUser().getId())
                        .username(post.getUser().getUsername())
                        .build())
                .upvotes((int) post.getPostVotes().stream().filter(v -> v.getVoteType() == VoteType.UPVOTE).count())
                .downvotes((int) post.getPostVotes().stream().filter(v -> v.getVoteType() == VoteType.DOWNVOTE).count())
                .comments( /* Optional: fetch level-1 comments */ null )
                .build();
    }

    public PostResponse createPostWithImage(Long userId, String title, String content, MultipartFile imageFile) throws IOException {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String imageUrl = null;
        if (imageFile != null && !imageFile.isEmpty()) {
            Map uploadResult = cloudinary.uploader().upload(imageFile.getBytes(), Map.of());
            imageUrl = (String) uploadResult.get("secure_url");
        }

        Post post = Post.builder()
                .title(title)
                .content(content)
                .user(user)
                .imageUrl(imageUrl)
                .build();

        postRepo.save(post);

        return PostResponse.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .createdAt(post.getCreatedAt().toString())
                .imageUrl(post.getImageUrl())
                .user(PostResponse.UserInfo.builder()
                        .id(user.getId())
                        .username(user.getUsername())
                        .build())
                .build();
    }

    // get all post of a user
    @Transactional
    public List<PostResponse> getPostsByUser(Long userId) {
        List<Post> posts = postRepo.findByUserIdOrderByCreatedAtDesc(userId);
        return posts.stream().map(post -> PostResponse.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .imageUrl(post.getImageUrl())
                .createdAt(post.getCreatedAt().toString())
                .user(PostResponse.UserInfo.builder()
                        .id(post.getUser().getId())
                        .username(post.getUser().getUsername())
                        .build())
                .build()).toList();
    }
}
