package in.ashokit.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import in.ashokit.dto.LoginDto;
import in.ashokit.dto.QuoteDto;
import in.ashokit.dto.RegisterDto;
import in.ashokit.dto.ResetPwdDto;
import in.ashokit.dto.UserDto;
import in.ashokit.entity.CityEntity;
import in.ashokit.entity.CountryEntity;
import in.ashokit.entity.StateEntity;
import in.ashokit.entity.UserEntity;
import in.ashokit.repo.CityRepo;
import in.ashokit.repo.CountryRepo;
import in.ashokit.repo.StateRepo;
import in.ashokit.repo.UserRepo;
import in.ashokit.utils.EmailUtils;
@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserRepo userRepo;

	@Autowired
	private CountryRepo countryRepo;

	@Autowired
	private StateRepo stateRepo;

	@Autowired
	private CityRepo cityRepo;

	@Autowired
	private EmailUtils emailUtils;
	
	private QuoteDto[] quotations = null;
	
	Random r = new Random();

	@Override
	public Map<Integer, String> getCountries() {

		Map<Integer, String> countryMap = new HashMap<>();

		List<CountryEntity> countriesList = countryRepo.findAll();

		countriesList.forEach(c -> {
			countryMap.put(c.getCountryId(), c.getCountryName());
		});

		return countryMap;
	}

	@Override
	public Map<Integer, String> getStates(Integer cid) {

		Map<Integer, String> statesMap = new HashMap<>();

		CountryEntity country = new CountryEntity();
		country.setCountryId(cid);

		StateEntity entity = new StateEntity();
		entity.setCountry(country);

		Example<StateEntity> of = Example.of(entity);
		List<StateEntity> statesList = stateRepo.findAll(of);

		statesList.forEach(s -> {
			statesMap.put(s.getStateId(), s.getStateName());
		});

		return statesMap;
	}

	@Override
	public Map<Integer, String> getCities(Integer sid) {

		Map<Integer, String> citiesMap = new HashMap<>();
		
		StateEntity state=new StateEntity();
		state.setStateId(sid);
		
		CityEntity city = new CityEntity();
		city.setState(state);
		
		Example<CityEntity> of = Example.of(city);
		List<CityEntity> citiesList = cityRepo.findAll(of);

		citiesList.forEach(c -> {
			citiesMap.put(c.getCityId(), c.getCityName());
		});

		return citiesMap;
	}

	@Override
	public UserDto getUser(String email) {

		UserEntity userEntity = userRepo.findByEmail(email);
		/*
		 * UserDto dto = new UserDto(); BeanUtils.copyProperties(userEntity, dto);
		 */

		if (userEntity == null) {
			return null;
		}

		ModelMapper mapper = new ModelMapper();
		UserDto userDto = mapper.map(userEntity, UserDto.class);

		return userDto;
	}

	@Override
	public boolean registerUser(RegisterDto regDto) {

		ModelMapper mapper = new ModelMapper();
		UserEntity entity = mapper.map(regDto, UserEntity.class);

		CountryEntity country = countryRepo.findById(regDto.getCountryId()).orElseThrow();

		StateEntity state = stateRepo.findById(regDto.getStateId()).orElseThrow();

		CityEntity city = cityRepo.findById(regDto.getCityId()).orElseThrow();

		entity.setCountry(country);
		entity.setState(state);
		entity.setCity(city);

		entity.setPwd(generatedRandom());
		entity.setPwdUpdate("no");

		UserEntity saveEntity = userRepo.save(entity);

		String subject = "User Registration";

		String body = "Your Temporary Pwd is" + entity.getPwd();

		emailUtils.sendEmail(regDto.getEmail(), subject, body);

		return saveEntity.getUserId() != null;
	}

	@Override
	public UserDto getUser(LoginDto loginDto) {

		UserEntity userEntity = userRepo.findByEmailAndPwd(loginDto.getEmail(), loginDto.getPwd());

		if (userEntity == null) {
			return null;
		}

		ModelMapper mapper = new ModelMapper();
		return mapper.map(userEntity, UserDto.class);
	}

	@Override
	public boolean resetPwd(ResetPwdDto pwdDto) {

		UserEntity userEntity = userRepo.findByEmailAndPwd(pwdDto.getEmail(), pwdDto.getOldPwd());

		if (userEntity != null) {
			userEntity.setPwd(pwdDto.getNewPwd());
			userEntity.setPwdUpdate("YES");
			userRepo.save(userEntity);
			return true;
		}
		return false;
	}

	@Override
	public String getQuote() {

      if(quotations == null) {		
		String url = "https//type.fit/api/quotes";

		// web service cell
		RestTemplate rt = new RestTemplate();
		ResponseEntity<String> forEntity = rt.getForEntity(url, String.class);
		String responseBody = forEntity.getBody();
		ObjectMapper mapper = new ObjectMapper();

		try {
			quotations = mapper.readValue(responseBody, QuoteDto[].class);
		} catch (Exception e) {
			e.printStackTrace();
		}
      }

		
		int index = r.nextInt(quotations.length - 1);
		return quotations[index].getText();

	}

	private String generatedRandom() {

		String aToZ = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
		
		StringBuilder res = new StringBuilder();
		for (int i = 0; i < 5; i++) {
			int randIndex = r.nextInt(aToZ.length());
			res.append(aToZ.charAt(randIndex));
		}

		return res.toString();
	}

}
