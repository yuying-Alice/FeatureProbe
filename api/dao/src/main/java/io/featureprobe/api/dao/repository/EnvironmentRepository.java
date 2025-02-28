package io.featureprobe.api.dao.repository;

import io.featureprobe.api.dao.entity.Environment;
import io.featureprobe.api.dao.entity.ServerEventEntity;
import io.featureprobe.api.dao.entity.ServerSegmentEntity;
import io.featureprobe.api.dao.entity.ServerToggleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface EnvironmentRepository extends JpaRepository<Environment, Long> {

    List<Environment> findAllByProjectKey(String projectKey);

    List<Environment> findByKeyIn(Set<String> key);

    List<Environment> findAllByProjectKeyAndArchivedOrderByCreatedTimeAsc(String projectKey, Boolean archived);

    long countByProjectKey(String projectKey);

    Optional<Environment> findByServerSdkKey(String serverSdkKey);

    Optional<Environment> findByServerSdkKeyOrClientSdkKey(String serverSdkKey, String clientSdkKey);

    boolean existsByProjectKeyAndKey(String projectKey, String key);

    boolean existsByProjectKeyAndName(String projectKey, String name);

    Optional<Environment> findByProjectKeyAndKey(String projectKey, String key);

    Optional<Environment> findByProjectKeyAndKeyAndArchived(String projectKey, String key, Boolean archived);

    List<Environment> findAllByArchivedAndDeleted(boolean archived, boolean deleted);

    @Query(value = "SELECT env.organization_id as organizationId,\n" +
            "                 env.project_key as  projectKey,\n" +
            "                 env.key as envKey,\n" +
            "                 env.client_sdk_key as clientSdkKey,\n" +
            "                 env.server_sdk_key as serverSdkKey,\n" +
            "                 env.version as envVersion, tg.key as toggleKey,\n" +
            "                 tg.return_type as returnType,\n" +
            "                 tg.client_availability as ClientAvailability,\n" +
            "                 ta.version as targetingVersion,\n" +
            "                 ta.disabled as targetingDisabled,\n" +
            "                 ta.content as targetingContent,\n" +
            "                 ta.publish_time as publishTime,\n" +
            "                 tcc.track_access_events as trackAccessEvents,\n" +
            "                 ta.modified_time as lastModified\n" +
            "          FROM environment env\n" +
            "              LEFT JOIN (SELECT * FROM toggle WHERE deleted = false AND archived = false) tg\n" +
            "                  ON env.organization_id = tg.organization_id AND env.project_key=tg.project_key\n" +
            "              LEFT JOIN (SELECT * FROM targeting WHERE deleted = false) ta\n" +
            "                   ON tg.organization_id=ta.organization_id AND\n" +
            "                                         tg.project_key = ta.project_key AND tg.key=ta.toggle_key\n" +
            "              LEFT JOIN toggle_control_conf tcc ON ta.organization_id = tcc.organization_id\n" +
            "                                                       AND ta.project_key = tcc.project_key\n" +
            "                                                       AND ta.environment_key=tcc.environment_key\n" +
            "                                                       AND ta.toggle_key = tcc.toggle_key\n" +
            "                   WHERE env.archived = false AND env.deleted = false",
            nativeQuery = true)
    List<ServerToggleEntity> findAllServerToggle();

    @Query(value = "SELECT pro.organization_id as organizationId, \n" +
            "       pro.key as projectKey, \n" +
            "       s.key as segmentKey,\n" +
            "       s.rules as segmentRules, \n" +
            "       s.version as segmentVersion, \n" +
            "       s.unique_key as segmentUniqueKey\n" +
            "FROM project pro INNER JOIN segment s on pro.organization_id=s.organization_id " +
            "AND pro.key = s.project_key WHERE pro.archived = false AND pro.deleted = false AND s.deleted = false",
            nativeQuery = true)
    List<ServerSegmentEntity> findAllServerSegment();


    @Query(value = "SELECT env.organization_id as organizationId,  \n" +
            "env.project_key as  projectKey,  \n" +
            "env.client_sdk_key as clientSdkKey,  \n" +
            "env.server_sdk_key as serverSdkKey,  \n" +
            "e.type as type,\n" +
            "e.name as name,  \n" +
            "e.matcher as matcher,  \n" +
            "e.url as url,  \n" +
            "e.selector as selector  \n" +
            "FROM environment env INNER JOIN metric m\n" +
            "ON env.organization_id = m.organization_id AND env.project_key = m.project_key\n" +
            "AND env.key = m.environment_key INNER JOIN metric_event me ON m.id = me.metric_id " +
            "INNER JOIN event e on me.event_id = e.id", nativeQuery = true)
    List<ServerEventEntity> findAllServerEvent();


    @Query(value = "SELECT env.organization_id as organizationId,\n" +
            "       env.project_key as  projectKey,\n" +
            "       env.client_sdk_key as clientSdkKey,\n" +
            "       env.server_sdk_key as serverSdkKey,\n" +
            "       e.type as type,\n" +
            "       e.name as name,\n" +
            "       e.matcher as matcher,\n" +
            "       e.url as url,\n" +
            "       e.selector as selector FROM environment env " +
            "INNER JOIN metric m ON env.organization_id = m.organization_id " +
            "AND env.project_key = m.project_key AND env.key = m.environment_key " +
            "INNER JOIN metric_event me ON m.id = me.metric_id " +
            "INNER JOIN event e on me.event_id = e.id WHERE env.server_sdk_key=?1", nativeQuery = true)
    List<ServerEventEntity> findAllServerEventBySdkKey(String sdkKey);

}
