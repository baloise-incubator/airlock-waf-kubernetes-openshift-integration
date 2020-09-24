package com.airlock.waf.client;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.airlock.waf.client.config.rs.client.AirlockWAFClient;

@SpringBootTest(classes = Application.class)
class DebugTests {

  private AirlockWAFClient client;
  private Context context;

  @Autowired
  public DebugTests(AirlockWAFClient client, Context context) {
    this.client = client;
    this.context = context;
  }

  @Test
  @Disabled
  void debug() {
    String cookie = client.session().create();
    client.configuration().loadBaseConfig(cookie);
    String backendGroupId = client.backendgroup().getKubernetesBackendGroupId(cookie);
  }

}
