package koreatech.cse.controller.oauth2;

import koreatech.cse.domain.oauth2.daum.DaumBook;
import koreatech.cse.domain.oauth2.daum.DaumProfile;
import koreatech.cse.domain.oauth2.daum.DaumProfileDetail;
import koreatech.cse.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.social.oauth2.AccessGrant;
import org.springframework.social.oauth2.GrantType;
import org.springframework.social.oauth2.OAuth2Parameters;
import org.springframework.social.oauth2.OAuth2Template;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;

@Controller
@RequestMapping("/oauth2")
public class DaumOauth2Controller {
    private static String daum_client_id = "3333162979934886926";      //본인 것으로 추가하기
    private static String daum_client_secret = "3c401b82ff9ed4d0c5c99c01d9cdf70e";  //본인 것으로 추가하기
    private static String daum_authorization_endpoint = "https://apis.daum.net/oauth2/authorize";
    private static String daum_token_endpoint = "https://apis.daum.net/oauth2/token";

    private static String daum_redirect_uri = "http://localhost:8080/oauth2/daum_callback";

    private static String daum_profile_url = "https://apis.daum.net/user/v1/show.json?access_token=";
    private static String daum_book_url = "https://apis.daum.net/search/book";

    private static String daum_rest_api_key = "db1fb4c3f1c0afb21cf29d189dea5e74";   //본인 것으로 추가하기

    @Inject
    private UserService userService;

    private OAuth2Template daumOAuth2Template = new OAuth2Template(
            daum_client_id,
            daum_client_secret,
            daum_authorization_endpoint,
            daum_token_endpoint
    );

    private HashMap<String, AccessGrant> daumAccessToken = new HashMap();

    @RequestMapping("/daum_authorization_code")
    public String daum_authorization_code() {
        OAuth2Parameters parameters = new OAuth2Parameters();
        parameters.setRedirectUri(daum_redirect_uri);

        String authorizeUrl = daumOAuth2Template.buildAuthorizeUrl(GrantType.AUTHORIZATION_CODE, parameters);
        System.out.println("authorizeUrl: " + authorizeUrl);

        return "redirect:" + authorizeUrl;
    }


    @RequestMapping("/daum_callback")
    public String daum_callback(Model model, HttpServletRequest request, @RequestParam String code) throws Exception {
        System.out.println("Daum Callback is called!!");
        System.out.println(code);

        SimpleClientHttpRequestFactory simpleClientHttpRequestFactory = new SimpleClientHttpRequestFactory();
        simpleClientHttpRequestFactory.setOutputStreaming(false);
        daumOAuth2Template.setRequestFactory(simpleClientHttpRequestFactory);

        MultiValueMap<String, String> multiValueMap = new LinkedMultiValueMap<String, String>();
        multiValueMap.add("client_id", daum_client_id);
        multiValueMap.add("client_secret", daum_client_secret);

        AccessGrant accessGrant = daumOAuth2Template.exchangeForAccess(
                code,
                daum_redirect_uri,
                multiValueMap
        );

        System.out.println("------ AccessGrant ------");
        System.out.println(accessGrant.getAccessToken());
        System.out.println(accessGrant.getRefreshToken());
        System.out.println(accessGrant.getExpireTime());
        System.out.println(accessGrant.getScope());
        System.out.println();

        DaumProfileDetail daumProfileDetail = getDaumProfile(accessGrant).getResult();
        if (daumProfileDetail != null) {
            daumAccessToken.put(daumProfileDetail.getUserid(), accessGrant);
        }

        return userService.daumLogin(request, daumProfileDetail);
    }

    public DaumProfile getDaumProfile(AccessGrant accessGrant) {
        RestTemplate restTemplate = new RestTemplate();
        DaumProfile daumProfile = null;
        try {
            ResponseEntity<DaumProfile> daumProfileResponseEntity
                    = restTemplate.getForEntity(daum_profile_url + accessGrant.getAccessToken(), DaumProfile.class);
            daumProfile = daumProfileResponseEntity.getBody();
            System.out.println(daumProfile);
        } catch (HttpClientErrorException e) {
            System.out.println(e.getStatusCode() + ": " + e.getStatusText());
        }
        return daumProfile;
    }

    @RequestMapping(value = "/daum_book_request", method = RequestMethod.GET)
    public String daumBookRequest(Model model, @RequestParam(name = "userId") String userId) {
        model.addAttribute("userId", userId);
        return "wsc/daumBookRequest";
    }

    @RequestMapping("/daumbook")
    public String getDaumBookWithJustAPIKey(Model model,
                              @RequestParam(name = "searchWord") String searchWord) {
        RestTemplate restTemplate = new RestTemplate();
        DaumBook daumBook = null;
        try {
            ResponseEntity<DaumBook> daumBookResponseEntity
                    = restTemplate.getForEntity(daum_book_url + "?q=" + searchWord + "&output=json" + "&apikey=" + daum_rest_api_key, DaumBook.class);
            daumBook = daumBookResponseEntity.getBody();
            System.out.println(daumBook);
        } catch (HttpClientErrorException e) {
            System.out.println(e.getStatusCode() + ": " + e.getStatusText());
        }
        model.addAttribute("searchWord", searchWord);
        model.addAttribute("daumBook", daumBook);
        return "wsc/daumBook";
    }
}
