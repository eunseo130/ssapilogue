package com.ssafy.ssapilogue.api.controller;

import com.ssafy.ssapilogue.api.dto.request.CreateProjectReqDto;
import com.ssafy.ssapilogue.api.dto.response.FindProjectDetailResDto;
import com.ssafy.ssapilogue.api.dto.response.FindProjectResDto;
import com.ssafy.ssapilogue.api.dto.response.FindProjectTitleResDto;
import com.ssafy.ssapilogue.api.exception.CustomException;
import com.ssafy.ssapilogue.api.exception.ErrorCode;
import com.ssafy.ssapilogue.api.service.BookmarkService;
import com.ssafy.ssapilogue.api.service.JwtTokenProvider;
import com.ssafy.ssapilogue.api.service.LikedService;
import com.ssafy.ssapilogue.api.service.ProjectService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api(tags = "Project", value = "프로젝트 API")
@RestController
@RequestMapping("/project")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;
    private final LikedService likedService;
    private final BookmarkService bookmarkService;
    private final JwtTokenProvider jwtTokenProvider;

    @GetMapping
    @ApiOperation(value = "프로젝트 전체조회", notes = "전체 프로젝트를 조회한다.")
    public ResponseEntity<Map<String, Object>> findProjects(
            HttpServletRequest request) {

        Map<String, Object> result = new HashMap<>();
        List<FindProjectResDto> projectList = null;

        String token = jwtTokenProvider.resolveToken(request);
        if (token == null) {
            projectList = projectService.findProjects("");
        } else {
            String userEmail = jwtTokenProvider.getUserEmail(token);
            if (userEmail == null) throw new CustomException(ErrorCode.WRONG_TOKEN);

            projectList = projectService.findProjects(userEmail);
        }

        result.put("projectList", projectList);

        return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
    }

    @PostMapping
    @ApiOperation(value = "프로젝트 등록", notes = "새로운 프로젝트를 등록한다.")
    public ResponseEntity<Map<String, Object>> createProject(
            @RequestBody @ApiParam(value = "프로젝트 정보", required = true) CreateProjectReqDto createProjectReqDto,
            HttpServletRequest request) {

        Map<String, Object> result = new HashMap<>();

        String token = jwtTokenProvider.resolveToken(request);
        if (token == null) throw new CustomException(ErrorCode.NO_TOKEN);

        String userEmail = jwtTokenProvider.getUserEmail(token);
        if (userEmail == null) throw new CustomException(ErrorCode.WRONG_TOKEN);

        Long projectId = projectService.createProject(createProjectReqDto, userEmail);
        result.put("projectId", projectId);

        return new ResponseEntity<Map<String, Object>>(result, HttpStatus.CREATED);
    }

    @GetMapping("/{projectId}")
    @ApiOperation(value = "프로젝트 상세조회", notes = "프로젝트를 조회한다.")
    public ResponseEntity<Map<String, Object>> findProjectDetail(
            @PathVariable @ApiParam(value = "프로젝트 id", required = true, example = "1") Long projectId,
            HttpServletRequest request) {

        Map<String, Object> result = new HashMap<>();

        String token = jwtTokenProvider.resolveToken(request);
        if (token == null) throw new CustomException(ErrorCode.NO_TOKEN);

        String userEmail = jwtTokenProvider.getUserEmail(token);
        if (userEmail == null) throw new CustomException(ErrorCode.WRONG_TOKEN);

        FindProjectDetailResDto project = projectService.findProjectDetail(projectId, userEmail);
        result.put("project", project);

        return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
    }

    @PutMapping("/{projectId}")
    @ApiOperation(value = "프로젝트 수정", notes = "프로젝트를 수정한다.")
    public ResponseEntity<Map<String, Object>> updateProject(
            @PathVariable @ApiParam(value = "프로젝트 id", required = true, example = "1") Long projectId,
            @RequestBody @ApiParam(value = "프로젝트 정보", required = true) CreateProjectReqDto createProjectReqDto,
            HttpServletRequest request) {

        Map<String, Object> result = new HashMap<>();

        String token = jwtTokenProvider.resolveToken(request);
        if (token == null) throw new CustomException(ErrorCode.NO_TOKEN);

        String userEmail = jwtTokenProvider.getUserEmail(token);
        if (userEmail == null) throw new CustomException(ErrorCode.WRONG_TOKEN);

        projectService.updateProject(projectId, createProjectReqDto);
        result.put("status", "SUCCESS");

        return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
    }

    @DeleteMapping("/{projectId}")
    @ApiOperation(value = "프로젝트 삭제", notes = "프로젝트를 삭제한다.")
    public ResponseEntity<Map<String, Object>> deleteProject(
            @PathVariable @ApiParam(value = "프로젝트 id", required = true, example = "1") Long projectId,
            HttpServletRequest request) {

        Map<String, Object> result = new HashMap<>();

        String token = jwtTokenProvider.resolveToken(request);
        if (token == null) throw new CustomException(ErrorCode.NO_TOKEN);

        String userEmail = jwtTokenProvider.getUserEmail(token);
        if (userEmail == null) throw new CustomException(ErrorCode.WRONG_TOKEN);

        projectService.deleteProject(projectId, userEmail);
        result.put("status", "SUCCESS");

        return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
    }

    @PostMapping("/{projectId}/like")
    @ApiOperation(value = "좋아요 등록", notes = "좋아요를 등록한다.")
    public ResponseEntity<Map<String, Object>> createLike(
            @PathVariable @ApiParam(value = "프로젝트 id", required = true, example = "1") Long projectId,
            HttpServletRequest request) {

        Map<String, Object> result = new HashMap<>();

        String token = jwtTokenProvider.resolveToken(request);
        if (token == null) throw new CustomException(ErrorCode.NO_TOKEN);

        String userEmail = jwtTokenProvider.getUserEmail(token);
        if (userEmail == null) throw new CustomException(ErrorCode.WRONG_TOKEN);

        likedService.createLiked(userEmail, projectId);
        result.put("status", "SUCCESS");

        return new ResponseEntity<Map<String, Object>>(result, HttpStatus.CREATED);
    }

    @DeleteMapping("/{projectId}/like")
    @ApiOperation(value = "좋아요 취소", notes = "좋아요를 취소한다.")
    public ResponseEntity<Map<String, Object>> deleteLike(
            @PathVariable @ApiParam(value = "프로젝트 id", required = true, example = "1") Long projectId,
            HttpServletRequest request) {

        Map<String, Object> result = new HashMap<>();

        String token = jwtTokenProvider.resolveToken(request);
        if (token == null) throw new CustomException(ErrorCode.NO_TOKEN);

        String userEmail = jwtTokenProvider.getUserEmail(token);
        if (userEmail == null) throw new CustomException(ErrorCode.WRONG_TOKEN);

        likedService.deleteLiked(userEmail, projectId);
        result.put("status", "SUCCESS");

        return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
    }

    @PostMapping("/{projectId}/bookmark")
    @ApiOperation(value = "북마크 등록", notes = "북마크를 등록한다.")
    public ResponseEntity<Map<String, Object>> createBookmark(
            @PathVariable @ApiParam(value = "프로젝트 id", required = true, example = "1") Long projectId,
            HttpServletRequest request) {

        Map<String, Object> result = new HashMap<>();

        String token = jwtTokenProvider.resolveToken(request);
        if (token == null) throw new CustomException(ErrorCode.NO_TOKEN);

        String userEmail = jwtTokenProvider.getUserEmail(token);
        if (userEmail == null) throw new CustomException(ErrorCode.WRONG_TOKEN);

        bookmarkService.createBookmark(userEmail, projectId);
        result.put("status", "SUCCESS");

        return new ResponseEntity<Map<String, Object>>(result, HttpStatus.CREATED);
    }

    @DeleteMapping("/{projectId}/bookmark")
    @ApiOperation(value = "북마크 취소", notes = "북마크를 취소한다.")
    public ResponseEntity<Map<String, Object>> deleteBookmark(
            @PathVariable @ApiParam(value = "프로젝트 id", required = true, example = "1") Long projectId,
            HttpServletRequest request) {

        Map<String, Object> result = new HashMap<>();

        String token = jwtTokenProvider.resolveToken(request);
        if (token == null) throw new CustomException(ErrorCode.NO_TOKEN);

        String userEmail = jwtTokenProvider.getUserEmail(token);
        if (userEmail == null) throw new CustomException(ErrorCode.WRONG_TOKEN);

        bookmarkService.deleteBookmark(userEmail, projectId);
        result.put("status", "SUCCESS");

        return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
    }

    @PutMapping("/{projectId}/readme")
    @ApiOperation(value = "리드미 갱신", notes = "리드미를 갱신한다.")
    public ResponseEntity<Map<String, Object>> updateReadme(
            @PathVariable @ApiParam(value = "프로젝트 id", required = true, example = "1") Long projectId,
            HttpServletRequest request) {

        Map<String, Object> result = new HashMap<>();

        String token = jwtTokenProvider.resolveToken(request);
        if (token == null) throw new CustomException(ErrorCode.NO_TOKEN);

        String userEmail = jwtTokenProvider.getUserEmail(token);
        if (userEmail == null) throw new CustomException(ErrorCode.WRONG_TOKEN);

        projectService.updateReadme(projectId);
        result.put("status", "SUCCESS");

        return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
    }

    @PostMapping("/image")
    @ApiOperation(value = "프로젝트 이미지 업로드", notes = "프로젝트 이미지를 업로드한다.")
    public ResponseEntity<Map<String, Object>> uploadImage(
            @RequestPart @ApiParam(value = "이미지 파일", required = true) MultipartFile file,
            HttpServletRequest request) {

        Map<String, Object> result = new HashMap<>();
        HttpStatus httpStatus = null;

        String token = jwtTokenProvider.resolveToken(request);
        if (token == null) throw new CustomException(ErrorCode.NO_TOKEN);

        String userEmail = jwtTokenProvider.getUserEmail(token);
        if (userEmail == null) throw new CustomException(ErrorCode.WRONG_TOKEN);

        try {
            String imageUrl = projectService.uploadImage(file, userEmail);
            httpStatus = HttpStatus.OK;
            result.put("imageUrl", imageUrl);
        } catch (Exception e) {
            e.printStackTrace();
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
            result.put("status", "SERVER ERROR");
        }
        return new ResponseEntity<Map<String, Object>>(result, httpStatus);
    }

    @GetMapping("/search")
    @ApiOperation(value = "제목으로 프로젝트 검색", notes = "제목으로 프로젝트를 검색한다.")
    public ResponseEntity<Map<String, Object>> searchProjectsByTitle(
            @RequestParam @ApiParam(value = "검색어") String keyword,
            HttpServletRequest request) {

        Map<String, Object> result = new HashMap<>();
        List<FindProjectResDto> projectList = null;

        String token = jwtTokenProvider.resolveToken(request);
        if (token == null) {
            projectList = projectService.searchProjectsByTitle(keyword, "");
        } else {
            String userEmail = jwtTokenProvider.getUserEmail(token);
            if (userEmail == null) throw new CustomException(ErrorCode.WRONG_TOKEN);

            projectList = projectService.searchProjectsByTitle(keyword, userEmail);
        }

        result.put("projectList", projectList);

        return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
    }

    @GetMapping("/search/title")
    @ApiOperation(value = "프로젝트 제목 자동완성", notes = "프로젝트 제목을 검색한다.")
    public ResponseEntity<Map<String, Object>> searchProjectTitles(
            @RequestParam @ApiParam(value = "검색어") String keyword) {

        Map<String, Object> result = new HashMap<>();

        List<FindProjectTitleResDto> searchList = projectService.searchProjectTitles(keyword);
        result.put("searchList", searchList);

        return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
    }
}
