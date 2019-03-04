package com.indrekvarva.merkletree.domain;

import com.guardtime.ksi.Signer;
import com.guardtime.ksi.SignerBuilder;
import com.guardtime.ksi.service.KSISigningClientServiceAdapter;
import com.guardtime.ksi.service.client.http.CredentialsAwareHttpSettings;
import com.guardtime.ksi.service.http.simple.SimpleHttpSigningClient;
import com.guardtime.ksi.unisignature.KSISignature;
import lombok.NoArgsConstructor;
import lombok.Value;

public class SigningService {

    private Signer signer;

    public SigningService() {
        //CredentialsAwareHttpSettings settings = new CredentialsAwareHttpSettings("signing-service-url", KSIServiceCredentials);
        //SimpleHttpSigningClient signingClient = new SimpleHttpSigningClient(settings);

        //this.signer = new SignerBuilder().setSigningService(new KSISigningClientServiceAdapter(signingClient)).build();

    }

    public void sign(Hash hash) {
        //KSISignature signature = this.signer.sign(hash.getValue());
    }
}
