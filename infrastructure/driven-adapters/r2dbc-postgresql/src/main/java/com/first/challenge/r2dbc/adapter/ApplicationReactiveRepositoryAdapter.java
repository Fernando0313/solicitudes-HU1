package com.first.challenge.r2dbc.adapter;

import com.first.challenge.model.application.Application;
import com.first.challenge.model.application.dto.PendingDecisionResponse;
import com.first.challenge.model.application.gateways.ApplicationRepository;
import com.first.challenge.model.criteria.SearchCriteria;
import com.first.challenge.r2dbc.helper.ReactiveCriteriaHelper;
import com.first.challenge.r2dbc.repository.ApplicationReactiveRepository;
import com.first.challenge.r2dbc.entity.ApplicationEntity;
import com.first.challenge.r2dbc.helper.ReactiveAdapterOperations;
import org.reactivecommons.utils.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.UUID;

@Repository
public class ApplicationReactiveRepositoryAdapter extends ReactiveAdapterOperations<
        Application,
        ApplicationEntity,
        UUID,
        ApplicationReactiveRepository
> implements ApplicationRepository {

    private static final Logger logger = LoggerFactory.getLogger(ApplicationReactiveRepositoryAdapter.class);
    private final TransactionalOperator operadorTransaccional;
    private final R2dbcEntityTemplate template;

    public ApplicationReactiveRepositoryAdapter(ApplicationReactiveRepository repository, ObjectMapper mapper,
                                                TransactionalOperator operadorTransaccional,
                                                R2dbcEntityTemplate template) {
        super(repository, mapper, d -> mapper.map(d, Application.class));

        this.operadorTransaccional = operadorTransaccional;
        this.template = template;
    }


    @Override
    public Mono<Application> save(Application application) {
        logger.info("[ApplicationReactiveRepositoryAdapter] Guardando solicitud email={}", application.getEmail());
        return  super.save(application)
                .doOnSuccess(saved -> logger.info("[ApplicationReactiveRepositoryAdapter.save] solicitud registrada id={} email={}", saved.getApplicationId(), saved.getEmail()))
                .doOnError(error -> logger.error("[ApplicationReactiveRepositoryAdapter.save] Error solicitud email={} - Causa: {}", application.getEmail(), error.getMessage(), error))
                .as(operadorTransaccional::transactional)
                .doFinally(signal -> logger.info("[ApplicationReactiveRepositoryAdapter.save] Finalizó flujo con señal={}", signal));
    }

    @Override
    public Mono<Application> updateState(Application application) {
        logger.info("[ApplicationReactiveRepositoryAdapter] Actualizacion solicitud email={}", application.getEmail());
        return  super.save(application)
                .doOnSuccess(saved -> logger.info("[ApplicationReactiveRepositoryAdapter.updateState] solicitud actualizada id={} email={}", saved.getApplicationId(), saved.getEmail()))
                .doOnError(error -> logger.error("[ApplicationReactiveRepositoryAdapter.updateState] Error solicitud email={} - Causa: {}", application.getEmail(), error.getMessage(), error))
                .as(operadorTransaccional::transactional)
                .doFinally(signal -> logger.info("[ApplicationReactiveRepositoryAdapter.updateState] Finalizó flujo con señal={}", signal));
    }


    @Override
    public Flux<PendingDecisionResponse> findByCriteria(SearchCriteria criteria) {
        ReactiveCriteriaHelper.WhereClause where = ReactiveCriteriaHelper.buildWhere(criteria.getFilters());

        String sql = """
    SELECT a.application_id,
           a.amount,
           a.term,
           a.email,
           a.loan_type_name            AS loan_type_name,
           a.interest_rate,
           a.state_name             AS state_name,
           a.base_salary               AS base_salary,
           a.monthly_amount               AS monthly_amount
    FROM v_pending_decisions a
    """ + (where.sql().isEmpty() ? "" : where.sql())+" " + """
    ORDER BY a.application_id ASC
    LIMIT :limit
    OFFSET :offset
    """;

        DatabaseClient.GenericExecuteSpec spec = template.getDatabaseClient()
                .sql(sql)
                .bind("limit", criteria.getSize())
                .bind("offset", (long) criteria.getPage() * criteria.getSize());

        /* bind de filtros dinámicos*/
        for (var e : where.params().entrySet()) {
            spec = spec.bind(e.getKey(), e.getValue());
        }

        return spec.map((row, md) -> PendingDecisionResponse.builder()
                        .applicationId(row.get("application_id", UUID.class))
                        .amount(row.get("amount", BigDecimal.class))
                        .term(row.get("term", Integer.class))
                        .email(row.get("email", String.class))
                        .loanTypeName(row.get("loan_type_name", String.class))
                        .interestRate(row.get("interest_rate", BigDecimal.class))
                        .stateName(row.get("state_name", String.class))
                        .baseSalary(row.get("base_salary", BigDecimal.class))
                        .monthlyAmount(row.get("monthly_amount", BigDecimal.class))
                        .build())
                .all();
    }

    @Override
    public Mono<Long> countByCriteria(SearchCriteria criteria) {
        ReactiveCriteriaHelper.WhereClause where = ReactiveCriteriaHelper.buildWhere(criteria.getFilters());

        String sql = """
        SELECT COUNT(*)
        FROM v_pending_decisions a
        """ + where.sql();

        DatabaseClient.GenericExecuteSpec spec = template.getDatabaseClient().sql(sql);
        for (var e : where.params().entrySet()) spec = spec.bind(e.getKey(), e.getValue());

        return spec.map((row, md) -> row.get(0, Long.class)).one();
    }
}
