package com.linkedin.thirdeye.alert.fetcher;

import com.linkedin.thirdeye.alert.commons.AnomalyFetcherConfig;
import com.linkedin.thirdeye.alert.commons.AnomalySource;
import com.linkedin.thirdeye.datalayer.DaoTestUtils;
import com.linkedin.thirdeye.datalayer.dto.AlertSnapshotDTO;
import com.linkedin.thirdeye.datalayer.bao.AnomalyFunctionManager;
import com.linkedin.thirdeye.datalayer.bao.DAOTestBase;
import com.linkedin.thirdeye.datalayer.bao.MergedAnomalyResultManager;
import com.linkedin.thirdeye.datalayer.dto.AnomalyFunctionDTO;
import com.linkedin.thirdeye.datalayer.dto.MergedAnomalyResultDTO;
import com.linkedin.thirdeye.datasource.DAORegistry;
import java.util.Collection;
import java.util.Properties;
import org.joda.time.DateTime;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;


public class TestUnnotifiedAnomalyFetcher {
  private static final String TEST = "test";
  private MergedAnomalyResultManager mergedAnomalyResultDAO;
  private AnomalyFunctionManager anomalyFunctionDAO;
  private DAOTestBase testDAOProvider;
  @BeforeClass
  public void beforeClass(){
    testDAOProvider = DAOTestBase.getInstance();
    DAORegistry daoRegistry = DAORegistry.getInstance();
    mergedAnomalyResultDAO = daoRegistry.getMergedAnomalyResultDAO();
    anomalyFunctionDAO = daoRegistry.getAnomalyFunctionDAO();


    AnomalyFunctionDTO anomalyFunction = DaoTestUtils.getTestFunctionSpec(TEST, TEST);
    anomalyFunction.setFilters("dimension=test;");
    long functionId = anomalyFunctionDAO.save(anomalyFunction);

    // Add mock anomalies
    MergedAnomalyResultDTO anomaly = DaoTestUtils.getTestMergedAnomalyResult(1l, 2l, TEST, TEST,
        -0.1, functionId, 1l);
    mergedAnomalyResultDAO.save(anomaly);

    anomaly = DaoTestUtils.getTestMergedAnomalyResult(3l, 4l, TEST, TEST,-0.2, functionId,
        3l);
    mergedAnomalyResultDAO.save(anomaly);
  }

  @AfterClass(alwaysRun = true)
  void afterClass() {
    testDAOProvider.cleanup();
  }

  @Test
  public void testGetAlertCandidates(){
    AlertSnapshotDTO alertSnapshot = DaoTestUtils.getTestAlertSnapshot();
    AnomalyFetcherConfig anomalyFetcherConfig = DaoTestUtils.getTestAnomalyFetcherConfig();

    AnomalyFetcher anomalyFetcher = new UnnotifiedAnomalyFetcher();
    anomalyFetcher.init(anomalyFetcherConfig);
    Collection<MergedAnomalyResultDTO>
        alertCandidates = anomalyFetcher.getAlertCandidates(new DateTime(2l), alertSnapshot);
    Assert.assertEquals(alertCandidates.size(), 1);
  }
}
