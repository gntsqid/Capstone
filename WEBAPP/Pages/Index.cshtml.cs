using Microsoft.AspNetCore.Mvc;
using Microsoft.AspNetCore.Mvc.RazorPages;
using CapstonePage.Models;
using System.Text.Json;

namespace CapstonePage.Pages
{
    public class IndexModel : PageModel
    {
        private readonly ILogger<IndexModel> _logger;
        private readonly IHttpClientFactory _httpClientFactory;

        public List<Machine> Machines { get; set; } = new();

        public IndexModel(ILogger<IndexModel> logger, IHttpClientFactory httpClientFactory)
        {
            _logger = logger;
            _httpClientFactory = httpClientFactory;
        }

        public async Task OnGetAsync()
        {
            Machines = await FetchMachines();
        }

        public async Task<JsonResult> OnGetMachinesJsonAsync()
        {
            var data = await FetchMachines();
            return new JsonResult(data);
        }

        private async Task<List<Machine>> FetchMachines()
        {
            var client = _httpClientFactory.CreateClient();
            var request = new HttpRequestMessage(HttpMethod.Get, "https://api.capstone.sqid.ink/<table_name>");

            var apiKey = Environment.GetEnvironmentVariable("CAPSTONE_API_SECRET");
            if (string.IsNullOrEmpty(apiKey))
            {
                _logger.LogError("CAPSTONE_API_SECRET environment variable is not set.");
                return new List<Machine>();
            }

            request.Headers.Add("X-API-Key", apiKey);

            var response = await client.SendAsync(request);
            if (!response.IsSuccessStatusCode)
            {
                _logger.LogError("API request failed with status: {StatusCode}", response.StatusCode);
                return new List<Machine>();
            }

            var stream = await response.Content.ReadAsStreamAsync();
            var machines = await JsonSerializer.DeserializeAsync<List<Machine>>(stream, new JsonSerializerOptions
            {
                PropertyNameCaseInsensitive = true
            });

            return machines?.Where(m => m.Type?.ToLower() == "sensor").ToList() ?? new List<Machine>();
        }
    }
}

