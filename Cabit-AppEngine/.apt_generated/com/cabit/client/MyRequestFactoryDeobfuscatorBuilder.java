// Automatically Generated -- DO NOT EDIT
// com.cabit.client.MyRequestFactory
package com.cabit.client;
import java.util.Arrays;
import com.google.web.bindery.requestfactory.vm.impl.OperationData;
import com.google.web.bindery.requestfactory.vm.impl.OperationKey;
public final class MyRequestFactoryDeobfuscatorBuilder extends com.google.web.bindery.requestfactory.vm.impl.Deobfuscator.Builder {
{
withOperation(new OperationKey("SIhgYSE_jU3Fphae0lFje3Hvado="),
  new OperationData.Builder()
  .withClientMethodDescriptor("(IZ)Lcom/google/web/bindery/requestfactory/shared/Request;")
  .withDomainMethodDescriptor("(IZ)Z")
  .withMethodName("UpdateOrder")
  .withRequestContext("com.cabit.shared.CabitRequest")
  .build());
withOperation(new OperationKey("oraaFoKKwUq5I1ZHZJ1__ZHCBnw="),
  new OperationData.Builder()
  .withClientMethodDescriptor("(Lcom/cabit/shared/GpsAddressProxy;Lcom/cabit/shared/GpsAddressProxy;)Lcom/google/web/bindery/requestfactory/shared/Request;")
  .withDomainMethodDescriptor("(Lcom/cabit/server/GpsAddress;Lcom/cabit/server/GpsAddress;)I")
  .withMethodName("CreateOrder")
  .withRequestContext("com.cabit.shared.CabitRequest")
  .build());
withOperation(new OperationKey("GOXEvqKYRV5$Z4ijtzjLAQmKzP0="),
  new OperationData.Builder()
  .withClientMethodDescriptor("(I)Lcom/google/web/bindery/requestfactory/shared/Request;")
  .withDomainMethodDescriptor("(I)Ljava/lang/String;")
  .withMethodName("GetOrderStatus")
  .withRequestContext("com.cabit.shared.CabitRequest")
  .build());
withOperation(new OperationKey("uth0O6TfgKBcaE5CInF7utl1uqg="),
  new OperationData.Builder()
  .withClientMethodDescriptor("(Lcom/cabit/shared/GpsLocationProxy;)Lcom/google/web/bindery/requestfactory/shared/Request;")
  .withDomainMethodDescriptor("(Lcom/cabit/server/GpsLocation;)V")
  .withMethodName("IAmNear")
  .withRequestContext("com.cabit.shared.CabitRequest")
  .build());
withOperation(new OperationKey("26njBJcvHGy9qVuqlylxR9n0zao="),
  new OperationData.Builder()
  .withClientMethodDescriptor("(Ljava/lang/String;)Lcom/google/web/bindery/requestfactory/shared/Request;")
  .withDomainMethodDescriptor("(Ljava/lang/String;)Lcom/cabit/server/Taxi;")
  .withMethodName("GetTaxi")
  .withRequestContext("com.cabit.shared.CabitRequest")
  .build());
withOperation(new OperationKey("UXmTcKM6MBBBqBgPsUqYTjgyNjs="),
  new OperationData.Builder()
  .withClientMethodDescriptor("()Lcom/google/web/bindery/requestfactory/shared/Request;")
  .withDomainMethodDescriptor("()Ljava/util/List;")
  .withMethodName("GetAllTaxi")
  .withRequestContext("com.cabit.shared.CabitRequest")
  .build());
withOperation(new OperationKey("OSRiBkcZVKqTyEK6SMoTTo1BUHA="),
  new OperationData.Builder()
  .withClientMethodDescriptor("(Lcom/cabit/shared/GpsLocationProxy;)Lcom/google/web/bindery/requestfactory/shared/Request;")
  .withDomainMethodDescriptor("(Lcom/cabit/server/GpsLocation;)Lcom/cabit/server/TaxiStatus;")
  .withMethodName("UpdateLocation")
  .withRequestContext("com.cabit.shared.CabitRequest")
  .build());
withRawTypeToken("gle_eq31UsxJsr5kS3wBQnJ0xeg=", "com.cabit.shared.GpsAddressProxy");
withRawTypeToken("_sPhDKt3dDlZECknqF1epuD9tOc=", "com.cabit.shared.GpsLocationProxy");
withRawTypeToken("5T2loT7YcyvGTZPYn0z$4B3JeBI=", "com.cabit.shared.OrderProxy");
withRawTypeToken("YekBLIQuKPyuuAuqDbIIuFhg$Lc=", "com.cabit.shared.RegistrationInfoProxy");
withRawTypeToken("pQLp_f42vx_3UxtRro92LueZQLc=", "com.cabit.shared.TaxiProxy");
withRawTypeToken("hPicMQchBnvu75eg6CmxvNP2cow=", "com.cabit.shared.TaxiStatusProxy");
withRawTypeToken("8KVVbwaaAtl6KgQNlOTsLCp9TIU=", "com.google.web.bindery.requestfactory.shared.ValueProxy");
withRawTypeToken("FXHD5YU0TiUl3uBaepdkYaowx9k=", "com.google.web.bindery.requestfactory.shared.BaseProxy");
withClientToDomainMappings("com.cabit.server.GpsAddress", Arrays.asList("com.cabit.shared.GpsAddressProxy"));
withClientToDomainMappings("com.cabit.server.GpsLocation", Arrays.asList("com.cabit.shared.GpsLocationProxy"));
withClientToDomainMappings("com.cabit.server.Order", Arrays.asList("com.cabit.shared.OrderProxy"));
withClientToDomainMappings("com.cabit.server.Taxi", Arrays.asList("com.cabit.shared.TaxiProxy"));
withClientToDomainMappings("com.cabit.server.TaxiStatus", Arrays.asList("com.cabit.shared.TaxiStatusProxy"));
}}
