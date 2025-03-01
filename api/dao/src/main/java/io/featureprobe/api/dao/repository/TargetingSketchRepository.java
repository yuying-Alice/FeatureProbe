package io.featureprobe.api.dao.repository;

import io.featureprobe.api.dao.entity.TargetingSketch;
import io.featureprobe.api.base.enums.SketchStatusEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface TargetingSketchRepository extends JpaRepository<TargetingSketch, Long>,
        JpaSpecificationExecutor<TargetingSketch> {

    List<TargetingSketch> findByApprovalIdIn(Set<Long> approvalIds);

    List<TargetingSketch> findByProjectKeyAndEnvironmentKeyAndStatusAndToggleKeyIn(String projectKey,
                                                                                   String environmentKey,
                                                                                   SketchStatusEnum status,
                                                                                   Set<String> toggleKeys);

    boolean existsByProjectKeyAndEnvironmentKeyAndStatus(String projectKey, String environmentKey,
                                                         SketchStatusEnum status);

}
